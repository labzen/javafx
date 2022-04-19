package cn.labzen.javafx

/**
 * todo 通过app.xml 或者 LynxJavaFX 指定该接口实现类
 */
interface LabzenApplicationEvent {

  /**
   * 通知有视图变更（包括stage的scene，和view内的子元素为容器的局部变更）
   */
  fun viewChanged(
    stageId: String,
    stageClass: Class<*>?,
    viewId: String,
    viewName: String,
    parameters: Map<String, Any>?
  )

}
