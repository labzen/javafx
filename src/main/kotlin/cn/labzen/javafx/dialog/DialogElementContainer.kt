package cn.labzen.javafx.dialog

/**
 * 弹框容器（视图），用于fxml的controller实现，方便弹框的事件监听
 */
interface DialogElementContainer {

  /**
   * 事件：弹框被弹出后调用，如果有动画，则动画效果完毕后调用
   */
  fun dialogOpened()

  /**
   * 事件：弹框被关闭后调用，如果有动画，则动画效果完毕后调用
   */
  fun dialogClosed()
}

