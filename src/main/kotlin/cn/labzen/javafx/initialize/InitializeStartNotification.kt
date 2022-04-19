package cn.labzen.javafx.initialize

import cn.labzen.cells.core.kotlin.toNullIfBlank

/**
 * 初始化器开始执行通知
 * @param message 初始化信息
 */
class InitializeStartNotification(
  initializer: Class<out LabzenApplicationInitializer>,
  private val message: String
) : LabzenInitializeNotification(initializer) {

  override fun notification(): String = message.toNullIfBlank() ?: DEFAULT_MESSAGE

  companion object {
    private const val DEFAULT_MESSAGE = "starting - 初始化开始执行"
  }
}
