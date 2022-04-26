package cn.labzen.javafx.stage

import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Stage
import javafx.stage.WindowEvent

/**
 * 标识一个独立窗口
 */
abstract class LabzenStage : LabzenStageContainer {

  private val id = StageHandler.generateStageId()
  private lateinit var stage: Stage

  internal fun setStage(stage: Stage) {
    this.stage = stage
  }

  final override fun id(): String = id

  final override fun instance(): Stage = stage

  override fun theme(): String? {
    // do nothing
    return null
  }

  override fun customize(primaryStage: Stage) {
    // do nothing
  }

  override fun closed(event: WindowEvent, primaryStage: Stage) {
    // do nothing
  }

  /**
   * 新窗口标题，为空时，与[Application]中提供的[Stage]标题相同
   */
  open fun title(): String? = null

  /**
   * 新窗口的任务栏图标，为空时与[Application]中提供的[Stage]图标相同
   */
  open fun icons(): List<Image>? = null

  /**
   * 主视图名（fxml文件名/路径，请忽略 '.fxml'），文件名/路径相对于 app.xml
   * 配置文件中的 "app/meta/structure/view"， 例如："user", "user/detail 即可
   */
  abstract fun primaryView(): String
}
