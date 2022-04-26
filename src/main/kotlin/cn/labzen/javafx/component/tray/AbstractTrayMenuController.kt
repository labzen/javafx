package cn.labzen.javafx.component.tray

import cn.labzen.javafx.stage.StageHandler.primaryStage
import javafx.application.Platform
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem

abstract class AbstractTrayMenuController : TrayMenuController {

  /**
   * 恢复窗口显示
   */
  private fun resumeStage() {
    Platform.runLater {
      val primaryStage = primaryStage().instance()
      if (primaryStage.isIconified) {
        primaryStage.isIconified = false
      }
      if (!primaryStage.isShowing) {
        primaryStage.show()
      }
      primaryStage.toFront()
    }
  }

  override fun clicked() {
    resumeStage()
  }

  override fun doubleClicked() {
    // do nothing
  }

  override fun secondaryClicked() {
    // do nothing
  }

  override fun createMenu(): ContextMenu? {
    // use default context menu
    return null
  }

  override fun createMenuItem(name: String, text: String, selectable: Boolean, provision: MenuItem): MenuItem {
    // use default menu item
    return provision
  }
}
