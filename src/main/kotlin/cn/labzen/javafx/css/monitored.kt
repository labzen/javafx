package cn.labzen.javafx.css

import cn.labzen.cells.core.utils.Randoms
import cn.labzen.javafx.stage.LabzenStageContainer
import cn.labzen.javafx.view.LabzenView
import cn.labzen.logger.kotlin.logger
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.Parent
import javafx.scene.Scene
import java.lang.ref.WeakReference
import java.net.URI
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern
import javafx.scene.Node as FxNode

internal object MonitoredTargetRegistry {

  private fun monitor(stylesheets: ObservableList<String>) {
    val wrapper = StylesheetsWrapper(stylesheets)
    stylesheets.addListener(StylesheetsListener(wrapper))

    if (stylesheets.isEmpty()) {
      return
    }

    stylesheets.filter { !isInJarFile(it) }.map {
      MonitoredStylesheets.createOrAppend(it, wrapper)
    }.forEach(MonitoredStylesheets::permeate)
  }

  private fun isInJarFile(uri: String): Boolean =
    "jar" == URI.create(uri).scheme

  private fun registerParent(parent: Parent) {
    monitor(parent.stylesheets)
    registerNodes(parent.childrenUnmodifiable)
  }

  fun registerScene(scene: Scene) {
    monitor(scene.stylesheets)
    registerParent(scene.root)
  }

  private fun registerNode(node: FxNode) {
    if (node is Parent) {
      registerParent(node)
    }
  }

  private fun registerNodes(nodes: ObservableList<FxNode>) {
    nodes.addListener(ListChangeListener {
      while (it.next()) {
        if (it.wasAdded()) {
          for (addedNode in it.addedSubList) {
            registerNode(addedNode)
          }
        }
      }
    })

    for (node in nodes) {
      registerNode(node)
    }
  }

  // ===================================================================================================================

  fun registerStage(instance: LabzenStageContainer) {
    val stage = instance.instance()
    stage.sceneProperty().addListener { _, _, new ->
      new?.also {
        registerScene(stage.scene)
      }
    }

    stage.scene?.run {
      registerScene(this)
    }
  }

  fun registerView(instance: LabzenView) {
    val parent = instance.wrapper().root
    parent.sceneProperty().addListener { _, _, new ->
      new?.also {
        registerScene(parent.scene)
      }
    }

    if (parent.scene != null) {
      registerScene(parent.scene)
    } else {
      registerParent(parent)
    }
  }
}

// =====================================================================================================================

internal class StylesheetsWrapper(stylesheets: ObservableList<String>) {
  val mark = Randoms.string(16)
  var updating: Boolean = false
  val target = WeakReference(stylesheets)
}

internal class StylesheetsListener(private val wrapper: StylesheetsWrapper) : ListChangeListener<String> {

  override fun onChanged(c: ListChangeListener.Change<out String>) {
    if (wrapper.updating) {
      return
    }

    while (c.next()) {
      if (c.wasAdded()) {
        for (uri in c.addedSubList) {
          MonitoredStylesheets.createOrAppend(uri, wrapper).permeate()
        }
      } else if (c.wasRemoved()) {
        for (uri in c.removed) {
          MonitoredStylesheets.abandon(uri, wrapper)
        }
      }
    }
  }
}

internal class MonitoredStylesheets private constructor(uri: String) {

  private val logger = logger { }

  val path: Path?
  private val unavailable: Boolean
  private val stylesheetsCollection = mutableListOf<StylesheetsWrapper>()

  init {
    val location = if (!URL_QUICK_MATCH.matcher(uri).matches()) {
      try {
        URL(uri).toString()
      } catch (e: Exception) {
        uri
      }
    } else {
      val classLoader = Thread.currentThread().contextClassLoader
      if (uri[0] == '/') {
        classLoader.getResource(uri.substring(1))
      } else {
        classLoader.getResource(uri)
      }?.toString() ?: uri
    }.let {
      if (it.startsWith("file:/")) {
        it.substring(6)
      } else it
    }

    path = Paths.get(location)
    unavailable = path == null
  }

  private fun append(stylesheets: StylesheetsWrapper): MonitoredStylesheets {
    if (!unavailable) {
      stylesheetsCollection.add(stylesheets)
    }
    return this
  }

  fun permeate() {
    if (unavailable) {
      return
    }

    CssHandler.registerMonitoredStylesheets(this)
    FileWatcher.watch(path!!)
  }

  fun avoid(stylesheets: StylesheetsWrapper) {
    if (unavailable) {
      return
    }

    stylesheetsCollection.removeIf {
      it.mark == stylesheets.mark
    }
    // 这里不能将本实例移除；如果移除了，等到文件如果有更新，会先监控到一个删除，然后才是添加和修改，就会产生在添加的时候，不知道这个文件应该应用到哪个stylesheets集合
  }

  fun applyFile(file: Path) {
    stylesheetsCollection.forEach {
      val uri = file.toUri().toURL().toExternalForm()
      val stylesheets = it.target.get()
      if (stylesheets?.contains(uri) == true) {
        it.updating = true
        val temp = stylesheets.toList()
        stylesheets.clear()
        stylesheets.addAll(temp)
        it.updating = false
      }
    }

    logger.info("CSS文件已重载 - $file")
  }

  companion object {
    // The logic of this method was taken from the class javafx.scene.image.Image
    private val URL_QUICK_MATCH = Pattern.compile("^\\p{Alpha}[\\p{Alnum}+.-]*:.*$")

    private val cached = mutableMapOf<String, MonitoredStylesheets>()

    fun createOrAppend(uri: String, stylesheets: StylesheetsWrapper): MonitoredStylesheets =
      cached.computeIfAbsent(uri) {
        MonitoredStylesheets(uri)
      }.append(stylesheets)

    fun abandon(uri: String, stylesheets: StylesheetsWrapper) {
      cached[uri]?.avoid(stylesheets)
    }

  }
}
