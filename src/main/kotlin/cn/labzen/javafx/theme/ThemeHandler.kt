package cn.labzen.javafx.theme

import cn.labzen.cells.core.kotlin.insureStartsWith
import cn.labzen.javafx.exception.DialogException
import cn.labzen.javafx.stage.LabzenStageContainer
import cn.labzen.javafx.stage.StageHandler
import cn.labzen.javafx.view.LabzenView
import cn.labzen.javafx.view.ViewHandler
import cn.labzen.javafx.view.ViewWrapper
import cn.labzen.logger.kotlin.logger
import javafx.collections.ObservableList
import javafx.scene.Parent
import javafx.scene.Scene
import java.io.File

object ThemeHandler {

  private val logger = logger { }
  private val themes = mutableMapOf<String, List<String>>()
  private var defaultTheme: String? = null

  /**
   * 更新全局默认皮肤目录路径
   */
  @JvmStatic
  fun updateDefaultTheme(location: String) {
    location.insureStartsWith("/").also {
      val stylesheets = parseThemeFolder(it)
      themes[it] = stylesheets ?: run {
        logger.warn("为能从默认皮肤路径中找到任何css文件: $location")
        listOf()
      }
      defaultTheme = it
      reapplyAllStageAndViewNow()
    }
  }

  private fun reapplyAllStageAndViewNow() {
    StageHandler.allStages().values.forEach(::reapply)
  }

  /**
   * 重置场景的皮肤，建议在[LabzenStageContainer.theme]中设定新的皮肤路径，而不是传入[location]参数（该参数如果提供，会被优先使用，
   * 但如果不改变[LabzenStageContainer.theme]的设定，这次操作将会是临时性的）
   *
   * @param stage [LabzenStageContainer]
   * @param location 可为空，使用[LabzenStageContainer.theme]指定的皮肤
   */
  @JvmStatic
  @JvmOverloads
  fun reapply(stage: LabzenStageContainer, location: String? = null) {
    val scene = stage.instance().scene
    applyTheme(scene, location ?: stage.theme())
  }

  /**
   * 重置视图的皮肤，建议在[viewId]对应的[LabzenView.theme]中设定新的皮肤路径，而不是传入[location]参数（该参数如果提供，会被优先使用，
   * 但如果不改变[LabzenView.theme]的设定，这次操作将会是临时性的）
   *
   * @param viewId 视图ID，程序会自动使用对应视图
   * @param location 可为空，使用[LabzenView.theme]指定的皮肤，如果其也为空，则会向上引用窗体stage的皮肤或全局默认皮肤
   */
  @JvmStatic
  @JvmOverloads
  fun reapply(viewId: String, location: String? = null) {
    val wrapper = ViewHandler.lookup(viewId) ?: throw DialogException("未找到ID为[$viewId]的视图信息")
    reapply(wrapper, location)
  }

  /**
   * 重置视图的皮肤，建议在参数[controller]的[LabzenView.theme]中设定新的皮肤路径，而不是传入[location]参数（该参数如果提供，会被优先使用，
   * 但如果不改变[LabzenView.theme]的设定，这次操作将会是临时性的）
   *
   * @param controller [LabzenView]
   * @param location 可为空，使用[LabzenView.theme]指定的皮肤，如果其也为空，则会向上引用窗体stage的皮肤或全局默认皮肤
   */
  @JvmStatic
  @JvmOverloads
  fun reapply(controller: LabzenView, location: String? = null) {
    reapply(controller.wrapper(), location)
  }

  /**
   * 重置视图的皮肤，建议在参数[wrapper]中controller（如果有的话）中设定新的皮肤路径，而不是传入[location]参数（该参数如果提供，会被优先使用，
   * 但如果不改变[LabzenView.theme]的设定，这次操作将会是临时性的）
   *
   * @param wrapper [LabzenView]
   * @param location 可为空，使用[LabzenView.theme]指定的皮肤，如果其也为空，则会向上引用窗体stage的皮肤或全局默认皮肤
   */
  @JvmStatic
  @JvmOverloads
  fun reapply(wrapper: ViewWrapper, location: String? = null) {
    if (wrapper.sceneCreated()) {
      applyTheme(wrapper.scene, location ?: wrapper.controller?.theme())
    } else {
      applyTheme(wrapper.root, location ?: wrapper.controller?.theme())
    }
  }

  @JvmStatic
  fun reapply(scene: Scene, location: String) {
    applyTheme(scene, location)
  }

  @JvmStatic
  fun reapply(container: Parent, location: String) {
    applyTheme(container, location)
  }

  @JvmStatic
  @JvmOverloads
  internal fun applyTheme(scene: Scene, location: String? = null) {
    applyTheme(scene.stylesheets, location)
  }

  @JvmStatic
  @JvmOverloads
  internal fun applyTheme(container: Parent, location: String? = null) {
    applyTheme(container.stylesheets, location)
  }

  private fun applyTheme(overlord: ObservableList<String>, location: String? = null) {
    overlord.clear()

    val stylesheets = if (location == null) {
      // 使用全局默认
      themes[defaultTheme]
    } else {
      themes.computeIfAbsent(location) {
        parseThemeFolder(location) ?: listOf()
      }
    }

    stylesheets?.forEach(overlord::add)
  }

  private fun parseThemeFolder(location: String): List<String>? {
    val resource = this.javaClass.getResource(location) ?: return null
    val folder = File(resource.file)
    if (!folder.isDirectory) {
      logger.warn("无法定位皮肤目录路径（路径应为 classpath 下的目录）")
      return null
    }

    val stylesheets = mutableListOf<File>()
    cssTraversal(folder, stylesheets)

    return stylesheets.map {
      it.toURI().toURL().toExternalForm()
    }.toList()
  }

  private fun cssTraversal(folder: File, stylesheets: MutableList<File>) {
    if (!folder.exists() || !folder.isDirectory) {
      return
    }

    folder.listFiles()?.forEach {
      if (it.isFile && "css" == it.extension) {
        stylesheets.add(it)
      } else if (it.isDirectory) {
        cssTraversal(it, stylesheets)
      }
    }
  }
}
