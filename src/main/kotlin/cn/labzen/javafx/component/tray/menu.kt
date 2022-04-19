package cn.labzen.javafx.component.tray

import cn.labzen.logger.kotlin.logger
import com.sun.javafx.scene.control.ContextMenuContent
import javafx.scene.control.CheckBox
import javafx.scene.control.CustomMenuItem
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import java.lang.reflect.Method

class SelectableMenuItem(
  text: String,
  hideOnClick: Boolean,
  functionInvoker: (selected: Boolean) -> Unit
) : CustomMenuItem() {

  private val label = Label(text)
  private val checkBox = CheckBox()

  init {
    val hBox = HBox(label, checkBox)

    checkBox.addEventFilter(MouseEvent.MOUSE_CLICKED) {
      functionInvoker(checkBox.isSelected)
    }

    hBox.parentProperty().addListener { _, _, n ->
      if (n is ContextMenuContent.MenuItemContainer) {
        n.addEventHandler(MouseEvent.MOUSE_CLICKED) {
          checkBox.isSelected = !checkBox.isSelected
          functionInvoker(checkBox.isSelected)
        }
      }
    }

    content = hBox
    isHideOnClick = hideOnClick

    styleClass.remove("custom-menu-item")
  }
}

internal open class MenuItemMeta(
  val name: String,
  val text: String,
  val group: Int
)

internal class MenuItemElement(
  name: String,
  text: String,
  group: Int,
  val order: Int,
  val selectable: Boolean,
  val method: Method
) : MenuItemMeta(name, text, group) {

  private val logger = logger { }

  var rightSelectableMethod: Boolean = false

  init {
    // 如果使用选项菜单项，看对应方法有没有唯一布尔参数来接收结果
    if (selectable) {
      checkSelectableMethodParameter(method)
    }
  }

  private fun checkSelectableMethodParameter(method: Method) {
    if (method.parameterCount == 1) {
      if (method.parameters[0].type == Boolean::class.java) {
        rightSelectableMethod = true
        return
      }
    }

    logger.warn("注解中标记了selectable的菜单项，方法[$method]应提供唯一一个布尔类型的参数，该方法将不能被触发")
  }
}

internal class MenuItemParent(
  name: String,
  text: String,
  group: Int,
  val items: ArrayList<MenuItemMeta> = ArrayList()
) : MenuItemMeta(name, text, group) {
  companion object {
    val ROOT = MenuItemParent("ROOT", "ROOT", -1)
  }
}
