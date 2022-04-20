package cn.labzen.javafx

import cn.labzen.javafx.config.App
import cn.labzen.javafx.config.ConfigurationLoader
import cn.labzen.javafx.exception.ApplicationBootException
import cn.labzen.javafx.preload.LabzenPreloader
import com.sun.javafx.application.LauncherImpl
import javafx.application.Platform
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.net.URL
import kotlin.system.exitProcess

object LabzenJavaFX {

  private val EMPTY_ARGS = arrayOf<String>()

  private val preBootProcesses = listOf(
    BasicProcess(),
    TrayProcess(),
    ThemeProcess(),
    CssProcess(),
    ShortcutsProcess()
  )

  /**
   * 启动，可指定配置文件，如不指定，则会按照 "/app.xml","/META-INF/app.xml" 的顺序查找，如无配置文件，无法启动
   */
  @JvmStatic
  @JvmOverloads
  fun boot(file: String? = null) {
    val springBootstrapClass = checkBootstrapClass()
    val configuration = loadConfiguration(file)

    LabzenPlatform.initialize(springBootstrapClass, configuration)
    createSpringApplication()

    preBootProcesses.forEach {
      it.process(configuration)
    }

    if (LabzenPlatform.container().enablePreload) {
      LauncherImpl.launchApplication(
        LabzenPlatform.container().fxApplicationClass,
        LabzenPreloader::class.java,
        EMPTY_ARGS
      )
    } else {
      LauncherImpl.launchApplication(LabzenPlatform.container().fxApplicationClass, EMPTY_ARGS)
    }
  }

  @JvmStatic
  fun terminate() {
    LabzenPlatform.shutdown()
    Platform.exit()
    exitProcess(0)
  }

  private fun checkBootstrapClass(): Class<*> {
    val cause = Thread.currentThread().stackTrace
    try {
      val caller: StackTraceElement = cause.first {
        it.methodName == "main"
            && Class.forName(it.className).isAnnotationPresent(SpringBootApplication::class.java)
      }
      return Class.forName(caller.className)
    } catch (e: NoSuchElementException) {
      throw ApplicationBootException("Labzen JavaFX的启动需要通过Spring Boot的main函数调起（别忘注解@SpringBootApplication）")
    }
  }

  private fun loadConfiguration(configuration: String?): App {
    val jc = this.javaClass
    val file: URL? = configuration?.run {
      jc.getResource(this)
    } ?: run {
      jc.getResource("/app.xml") ?: jc.getResource("/META-INF/app.xml")
    }

    file ?: throw ApplicationBootException("找不到配置文件：${configuration ?: "app.xml"}")
    return ConfigurationLoader.load(file)
  }

  private fun createSpringApplication() {
    try {
      val springBootstrapClass = LabzenPlatform.container().springBootstrapClass
      val context = SpringApplication.run(springBootstrapClass)

      LabzenPlatform.container().springApplicationContext.set(context)
    } catch (throwable: Throwable) {
      throw ApplicationBootException(throwable, "Spring Boot启动失败")
    }
  }
}
