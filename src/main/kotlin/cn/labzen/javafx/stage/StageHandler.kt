package cn.labzen.javafx.stage

import cn.labzen.cells.core.utils.Randoms
import cn.labzen.javafx.LabzenPlatform
import cn.labzen.javafx.exception.StageViewOperationException
import cn.labzen.logger.kotlin.logger
import com.sun.javafx.stage.StageHelper
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.stage.Stage
import org.springframework.beans.BeansException

object StageHandler {

  private val logger = logger { }

  private lateinit var mainStage: LabzenStageContainer
  private val stages = FXCollections.observableHashMap<String, LabzenStageContainer>()
  private val stageSceneHistories = mutableMapOf<String, StageViewHistory>()

  internal fun setPrimaryStage(stage: LabzenStageContainer) {
    mainStage = stage

    stages.computeIfAbsent(stage.id()) { stage }
    stageSceneHistories.computeIfAbsent(stage.id()) { StageViewHistory() }
    StageHelper.setPrimary(stage.instance(), true)
  }

  /**
   * 切换到新的主窗体
   * @param stage 将要成为新的主窗体
   * @return 返回原主窗体ID
   */
  @JvmStatic
  fun changePrimaryStage(stage: LabzenStageContainer): LabzenStageContainer {
    val originalMain = mainStage
    setPrimaryStage(stage)
    return originalMain
  }

  internal fun primaryStage() =
    mainStage

  internal fun allStages() =
    stages

  /**
   * 创建新的窗口
   */
  @JvmStatic
  fun createStage(cls: Class<out LabzenStage>): LabzenStage {
    val sac = LabzenPlatform.container().springApplicationContext.get()
    val ls = try {
      sac.getBean(cls)
    } catch (e: BeansException) {
      sac.autowireCapableBeanFactory.createBean(cls)
    }

    stages[ls.id()] = ls
    stageSceneHistories[ls.id()] = StageViewHistory()

    val stage = Stage().also {
      val container = LabzenPlatform.container()
      val icons = ls.icons() ?: container.icons
      val title = ls.title() ?: container.appTitle

      it.icons.addAll(icons)
      it.title = title
    }

    ls.setStage(stage)
    stage.setOnCloseRequest {
      ls.closed(it, stage)
    }

    ls.customize(stage)
    goNow(ls.id(), ls.primaryView())
    return ls
  }

  /**
   * 获取已创建的窗口
   */
  @JvmStatic
  fun stage(stageId: String): LabzenStage? {
    return stages[stageId]?.let { it as LabzenStage }
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
      goNow(stageId, viewMark, parameters)
    }
  }

  internal fun goNow(
    stageId: String? = null,
    viewMark: String,
    parameters: Map<String, Any>? = null
  ) {
    val stage = stageId?.let {
      stages[stageId] ?: throw StageViewOperationException("不存在的窗口ID：$stageId")
    } ?: mainStage

    val history = stageSceneHistories[stage.id()]!!
    val wrapper = history.loadAndPush(viewMark, parameters)
    wrapper.attachTo(stage)
    wrapper.createScene()
    stage.instance().scene = wrapper.scene
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
      val stage = stageId?.let {
        stages[stageId] ?: throw StageViewOperationException("不存在的窗口ID：$stageId")
      } ?: mainStage

      val history = stageSceneHistories[stage.id()]!!
      val wrapper = history.searchAndPop(viewMark, parameters)
      wrapper?.apply {
        wrapper.reapplyThemeIfNecessary()
        stage.instance().scene = this.scene
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
