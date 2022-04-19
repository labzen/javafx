package cn.labzen.javafx.initialize

import cn.labzen.cells.core.kotlin.toNullIfBlank

/**
 * 初始化器完成通知
 * @param progress 进度
 * @param message 初始化信息
 * @param elapsed 耗时，单位：毫秒
 */
class InitializeFinishedNotification(
  initializer: Class<out LabzenApplicationInitializer>,
  val progress: Double,
  val elapsed: Int,
  private val message: String?
) : LabzenInitializeNotification(initializer) {

  override fun notification(): String = message?.toNullIfBlank() ?: DEFAULT_MESSAGE

  companion object {
    private const val DEFAULT_MESSAGE = "finished - 初始化执行完毕"
  }
}
