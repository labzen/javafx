package cn.labzen.javafx.dialog

import javafx.event.ActionEvent
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.Pane
import javafx.stage.Modality
import javafx.stage.StageStyle

/**
 * 弹窗，以窗体的形式的弹框，拥有独立窗体，可遮挡其他窗体
 */
class DialogWindow internal constructor(alertType: AlertType, buttons: List<ButtonType>? = null) : Alert(alertType) {

  init {
    initStyle(StageStyle.UTILITY)
    initModality(Modality.APPLICATION_MODAL)
    isResizable = false

    dialogPane = AlertDialogPane(alertType, buttons)
  }

  internal fun expandContent(content: Node) {
    dialogPane.expandableContent = content
    dialogPane.isExpanded = true
    isResizable = false

    if (headerText == null && contentText == null) {
      dialogPane.header = Pane().apply {
      }
    }
  }

  inner class AlertDialogPane(alertType: AlertType, customButtons: List<ButtonType>?) : DialogPane() {

    private val uselessButtonType = ButtonType(null, ButtonBar.ButtonData.OTHER)
    private val uselessButton = Button("").apply { isVisible = false }

    init {
      val buttons = if (customButtons?.isNotEmpty() == true) {
        customButtons.toTypedArray()
      } else {
        when (alertType) {
          AlertType.NONE -> arrayOf(uselessButtonType)
          AlertType.ERROR -> arrayOf(uselessButtonType, ButtonType.OK)
          AlertType.CONFIRMATION -> arrayOf(ButtonType.OK, ButtonType.CANCEL)
          else -> arrayOf(ButtonType.OK)
        }
      }

      buttonTypes.addAll(buttons)
      styleClass.addAll("alert", alertType.toString().toLowerCase())
    }

    override fun createDetailsButton(): Node {
      return Hyperlink().apply {
        this.isVisible = false
      }
    }

    override fun createButton(buttonType: ButtonType): Node =
      if (uselessButtonType == buttonType) {
        /* FXDialog.requestPermissionToClose() 在点击弹出框 'X' 关闭按钮时，会判断：
           如果没有任何按钮 或 有两个及以上但都不是 cancel button，则不会关闭；
           在错误弹框等特殊情况下，因为默认只有一个确认按钮，想要屏蔽弹框的 'X' 关闭按钮，
           默认添加一个隐藏的 uselessButtonType 来变相实现 */
        uselessButton
      } else {
        Button(buttonType.text).apply {
          val data = buttonType.buttonData
          ButtonBar.setButtonData(this, data)

          isDefaultButton = data.isDefaultButton
          isCancelButton = data.isCancelButton

          addEventHandler(ActionEvent.ACTION) {
            if (it.isConsumed) return@addEventHandler
            result = buttonType
            close()
          }
        }
      }
  }
}
