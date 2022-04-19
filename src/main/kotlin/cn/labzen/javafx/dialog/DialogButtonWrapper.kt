package cn.labzen.javafx.dialog

import javafx.event.Event
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import java.util.function.Function

class DialogButtonWrapper internal constructor(val button: Button) {

  /**
   * 当按钮被点击，或在按钮上按回车键时，出发的处理逻辑。Function部分需要返回一个布尔值，返回true，会将当前弹框关闭
   */
  fun on(function: Function<Event, Boolean>) {
    val handler: (event: Event) -> Unit = {
      val ignore = it is KeyEvent && it.code != KeyCode.ENTER

      if (!ignore) {
        val close = function.apply(it)
        if (!close) {
          it.consume()
        }
      }
    }
    button.addEventFilter(MouseEvent.MOUSE_PRESSED, handler)
    button.addEventFilter(KeyEvent.KEY_PRESSED, handler)
  }
}
