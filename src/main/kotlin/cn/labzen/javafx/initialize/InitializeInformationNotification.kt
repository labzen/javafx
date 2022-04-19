package cn.labzen.javafx.initialize

/**
 * 初始化器执行中信息通知
 */
class InitializeInformationNotification(
  initializer: Class<out LabzenApplicationInitializer>,
  val message: String,
  val progress: Double,
) : LabzenInitializeNotification(initializer) {

  override fun notification(): String = message
}
