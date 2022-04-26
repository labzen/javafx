package cn.labzen.javafx.dialog

object DialogHandler {

  /**
   * 创建**弹窗**Builder，以窗体的形式的弹框，拥有独立窗体，可遮挡其他窗体
   *
   * 注意：**弹窗！ 弹窗！ 弹窗！**
   */
  @JvmStatic
  fun createWindowBuilder() =
    DialogWindowBuilder()

  /**
   * 创建**弹框**Builder，以蒙层方式弹出的弹框，类似网页的弹框
   *
   * 注意：**弹框！ 弹框！ 弹框！**
   */
  @JvmStatic
  fun createElementBuilder() =
    DialogElementBuilder()
}
