package cn.labzen.javafx.stage

import com.sun.javafx.util.Utils
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Rectangle2D
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.Window
import javafx.stage.WindowEvent

/**
 * 框架内部使用
 */
interface LabzenStageContainer {

  fun id(): String

  fun instance(): Stage

  /**
   * 窗体应用的皮肤 ***目录***  路径，无皮肤路径指定，使用全局默认皮肤样式 - (通过 app.xml 设定)
   */
  fun theme(): String?

  // todo customize 和 closed 只在 Application 中应用，还未在普通窗体 Stage 中使用
  /**
   * 对 Stage 窗口进行定制化，在窗口显示前被调用 void start(Stage primaryStage)
   */
  fun customize(primaryStage: Stage)

  /**
   * 窗口退出时被调用，可处理停闭服务、释放资源等操作，请注意范围（尽量控制在窗口相关的资源、服务）
   */
  fun closed(event: WindowEvent, primaryStage: Stage)

  /**
   * 显示并屏幕居中
   */
  fun showAndCenterOnScreen() {
    instance().onShown = EventHandler {
      centerOnScreen()
    }
    instance().show()
  }

  fun centerOnScreen() {
    Platform.runLater {
      val stage = instance()
      val bounds: Rectangle2D = getWindowScreen().visualBounds
      stage.x = (bounds.width - stage.width) / 2
      stage.y = (bounds.height - stage.height) / 2
    }
  }

  private fun getWindowScreen(): Screen {
    val window: Window = instance()
    if (!java.lang.Double.isNaN(window.x)
      && !java.lang.Double.isNaN(window.y)
      && !java.lang.Double.isNaN(window.width)
      && !java.lang.Double.isNaN(window.height)
    ) {
      return Utils.getScreenForRectangle(
        Rectangle2D(
          window.x,
          window.y,
          window.width,
          window.height
        )
      )
    } else {
      return Screen.getPrimary()
    }
  }
}
