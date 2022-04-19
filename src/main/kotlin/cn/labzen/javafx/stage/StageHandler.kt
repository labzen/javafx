package cn.labzen.javafx.stage

import cn.labzen.javafx.exception.StageViewOperationException
import cn.labzen.logger.kotlin.logger
import javafx.application.Platform
import javafx.collections.FXCollections

object StageHandler {

  private val logger = logger { }

  private const val PRIMARY_STAGE_KEY = "__MAIN_STAGE__"
  private val stages = FXCollections.observableHashMap<String, LabzenStage>()
  private val stageSceneHistories = mutableMapOf<String, StageViewHistory>()

  internal fun setPrimaryStage(stage: LabzenStage) {
    stages[PRIMARY_STAGE_KEY] = stage
    stageSceneHistories[PRIMARY_STAGE_KEY] = StageViewHistory()
  }

  internal fun getPrimaryStage() =
    stages[PRIMARY_STAGE_KEY]

  internal fun allStages() =
    stages

  fun createStage(id: String): LabzenStage? {
    // todo 创建stage时，带一个history
    stageSceneHistories[""] = StageViewHistory()
    return null
  }

  /**
   * 获取已创建的窗口
   */
  fun stage(stageId: String): LabzenStage? {
    return null
  }

  //，如 [cached] = true，并在视图栈中已存在该视图，则将该视图实例移至栈顶；如想同时存在多个相同的视图在栈中，则 [cached] 指为false
  // @return 视图在窗口的视图栈中的唯一ID，可在视图栈中找到准确的位置，当视图被弹出（back）后销毁
  /**
   * 当前窗体更新到新的视图
   *
   * @param stageId 独立窗口ID，通过 [createStage] 方法获取，如需操作主窗口，则不需要此参数
   * @param viewMark 视图名（fxml文件名/路径，请忽略 '.fxml'），文件名/路径相对于 app.xml 配置文件中的 "app/meta/structure/view"，
   *             例如："user", "user/detail 即可
   * @param parameters 需要传递视图的参数
   */
  @JvmStatic
  @JvmOverloads
  fun go(
    stageId: String? = null,
    viewMark: String,
    parameters: Map<String, Any>? = null
  ) {
    Platform.runLater {
      val stageKey = stageId ?: PRIMARY_STAGE_KEY
      val stage = stages[stageKey]
      stage ?: throw StageViewOperationException("不存在的窗口ID：$stageKey")

      val history = stageSceneHistories[stageKey]!!
      val wrapper = history.loadAndPush(viewMark, parameters)
      wrapper.attachTo(stage)
      wrapper.createScene()
      stage.getStage().scene = wrapper.scene
    }
  }

  /**
   * 当前窗体回到上一个或某一个视图，[viewMark] 不指定时，将显示最后一个视图，类似浏览器 back(1)的意思
   *
   * @param stageId 独立窗口ID，通过 [createStage] 方法获取，如需操作主窗口，则不需要此参数
   * @param viewMark 视图的ID（可准确定位到一个视图在视图栈中的位置），或视图名（fxml文件名/路径，请忽略 '.fxml'；配置文件中的 "app/meta/structure/view"，
   *             例如："user", "user/detail 即可；如在窗口的视图栈中存在多个相同的view，则默认使用视图栈中最新的那个）
   * @param parameters 需要传递视图的参数
   */
  @JvmStatic
  @JvmOverloads
  fun back(
    stageId: String? = null,
    viewMark: String? = null,
    parameters: Map<String, Any>? = null
  ) {
    Platform.runLater {
      val stageKey = stageId ?: PRIMARY_STAGE_KEY
      val stage = stages[stageKey]
      stage ?: throw StageViewOperationException("不存在的窗口ID：$stageKey")

      val history = stageSceneHistories[stageKey]!!
      val wrapper = history.searchAndPop(viewMark, parameters)
      wrapper?.apply {
        wrapper.reapplyThemeIfNecessary()
        stage.getStage().scene = this.scene
      } ?: logger.warn("无法执行视图 back() for - stage_id: $stageId, view_mark: $viewMark")
    }
  }

  /**
   * 当前窗体回到上一个，类似浏览器 back(1)的意思
   *
   * @param stageId 独立窗口ID，通过 [createStage] 方法获取，如需操作主窗口，则不需要此参数
   * @param parameters 需要传递视图的参数
   */
  @JvmStatic
  @JvmOverloads
  fun back(
    stageId: String? = null,
    parameters: Map<String, Any>
  ) {
    back(stageId, null, parameters)
  }

  /**
   * 当前窗体替换一个已存在的视图，如不存在，则操作不生效；操作完成后，视图栈的size不改变，未涉及到的其他视图原位置不变
   *
   * @param stageId 独立窗口ID，通过 [createStage] 方法获取，如需操作主窗口，则不需要此参数
   * @param viewId 视图的ID，通过 go 方法获取，可准确定位到一个视图在视图栈中的位置；不指定该参数，则认为是替换当前视图
   * @param viewName 视图名（fxml文件名/路径，请忽略 '.fxml'），文件名/路径相对于 app.xml 配置文件中的 "app/meta/structure/view"，
   *             例如："user", "user/detail 即可
   * @param  parameters 需要传递视图的参数
   *
   * @return 视图替换掉原 [viewId] 的视图后，在窗口的视图栈中的唯一ID，位置与原 [viewId] 的视图位置一致，当视图被弹出（back）后销毁
   */
  @JvmStatic
  @JvmOverloads
  fun replace(
    stageId: String? = null,
    viewId: String? = null,
    viewName: String,
    parameters: Map<String, Any>? = null
  ): String {
    return ""
  }
}
