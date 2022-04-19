package cn.labzen.javafx.initialize

/**
 * 初始化器执行失败通知
 * @param throwable 失败一次
 * @param elapsed 耗时
 * @param exit 是否直接退出应用
 */
class InitializeFailedNotification(
  initializer: Class<out LabzenApplicationInitializer>,
  val throwable: Throwable,
  val progress: Double,
  val elapsed: Int,
  val exit: Boolean
) : LabzenInitializeNotification(initializer) {

  override fun notification(): String = throwable.message ?: UNKNOWN_EXCEPTION_MESSAGE

  companion object {
    private const val UNKNOWN_EXCEPTION_MESSAGE = "未知异常"
  }
}
