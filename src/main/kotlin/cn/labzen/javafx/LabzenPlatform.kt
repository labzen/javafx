package cn.labzen.javafx

import cn.labzen.cells.core.kotlin.InitOnceProperty
import cn.labzen.cells.core.kotlin.insureEndsWith
import cn.labzen.cells.core.kotlin.insureStartsWith
import cn.labzen.cells.core.kotlin.throwRuntimeUnless
import cn.labzen.javafx.config.App
import cn.labzen.javafx.exception.ApplicationBootException
import cn.labzen.javafx.exception.ApplicationException
import cn.labzen.javafx.preload.PreloadDetails
import cn.labzen.logger.kotlin.logger
import javafx.application.Platform
import javafx.scene.image.Image
import org.springframework.context.ConfigurableApplicationContext
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.FutureTask


object LabzenPlatform {

  private val logger = logger { }

  private val container = InitOnceProperty<Container>()

  internal fun container() =
    container.get()

  internal fun initialize(springBootstrapClass: Class<*>, config: App) {
    container.set(Container(config, springBootstrapClass))
  }

  /**
   * 结束应用进程
   */
  internal fun shutdown() {
    val c = container.getOrNull() ?: throw IllegalStateException("WTF! 肿木阔能")
    val sac = c.springApplicationContext.getOrNull() ?: throw IllegalStateException("WTF! 肿木阔能")
    sac.close()
  }

  /**
   * 在JFX线程中同步运行，调用该方法的线程将被阻塞
   */
  @JvmStatic
  fun runAndWait(runnable: Runnable) {
    if (Platform.isFxApplicationThread()) {
      runnable.run()
    } else {
      val ft = FutureTask<Void>(runnable, null)
      try {
        Platform.runLater(ft)
        ft.get()
      } catch (e: Exception) {
        logger.warn(e, "同步阻塞线程执行异常")
      }
    }
  }

  /**
   * 在JFX线程中同步运行，调用该方法的线程将被阻塞
   */
  @JvmStatic
  fun <V> runAndWait(callable: Callable<V>): V =
    if (Platform.isFxApplicationThread()) {
      callable.call()
    } else {
      val ft = FutureTask(callable)
      try {
        Platform.runLater(ft)
        ft.get()
      } catch (e: Exception) {
        throw ApplicationException(e, "同步阻塞线程执行异常")
      }
    }

  /**
   * 返回第一个存在的资源URL
   */
  internal fun resource(vararg locations: String): URL? {
    val fac = container().fxApplicationClass
    return locations.mapNotNull {
      fac.getResource(it)
    }.firstOrNull()
  }

  internal data class Container(
    private val config: App,
    /**
     * Spring的启动类，不应与JavaFX启动类是同一个类
     */
    val springBootstrapClass: Class<*>
  ) {
    private val logger = logger { }

    /**
     * JavaFX的启动类，不应与Spring启动类是同一个类
     */
    val fxApplicationClass: Class<out LabzenApplication> =
      config.meta.bootstrap.application.let {
        val cls = try {
          Class.forName(it)
        } catch (e: Exception) {
          throw ApplicationBootException(e, "找不到 JavaFX Application 类：$it")
        }

        val isValid = LabzenApplication::class.java.isAssignableFrom(cls)
        isValid.throwRuntimeUnless() {
          ApplicationBootException(""" 配置文件 "app/meta/bootstrap#application-class" 指向的类必须继承自 cn.labzen.javafx.LabzenApplication """)
        }
        @Suppress("UNCHECKED_CAST")
        cls as Class<out LabzenApplication>
      }
    val springApplicationContext = InitOnceProperty<ConfigurableApplicationContext>()

    // 应用的所有图标
    lateinit var icons: List<Image>
    val appTitle = config.meta.title

    private val structure = config.meta.structure
    val resourceViewPath = structure.view.insureEndsWith("/").insureStartsWith("/")

    val enablePreload = "true".equals(config.meta.bootstrap.needPreload, true)
    val preloadView = run {
      val bootstrap = config.meta.bootstrap
      if ("false".equals(bootstrap.needPreload, true)) {
        null
      } else {
        bootstrap.preload?.view?.trimStart('/')
          ?: throw ApplicationBootException("""未找到 "app/meta/bootstrap/preload" 的配置 """)
      }
    }
    val primaryView = config.meta.bootstrap.view.trimStart('/')
    val preloadDetails = InitOnceProperty<PreloadDetails>()

    fun loadIconsIfNecessary() {
      if (this::icons.isInitialized) {
        return
      }

      val assignedIcons = config.meta.bootstrap.icons?.split(",")
      val appliedIcons = assignedIcons?.ifEmpty { defaultIcons } ?: defaultIcons

      icons = appliedIcons.mapNotNull {
        val resource = resource(it)
        if (resource == null) {
          logger.warn("找不到图标文件 - $it")
          null
        } else {
          Image(resource.toExternalForm())
        }
      }
    }

    companion object {
      private val defaultIcons = listOf(
        "/icon/default_16x16.png",
        "/icon/default_20x20.png",
        "/icon/default_24x24.png",
        "/icon/default_32x32.png",
        "/icon/default_48x48.png",
        "/icon/default_64x64.png",
        "/icon/default_128x128.png"
      )
    }
  }

}
