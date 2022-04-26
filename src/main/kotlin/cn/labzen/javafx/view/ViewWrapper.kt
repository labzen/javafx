package cn.labzen.javafx.view

import cn.labzen.cells.core.kotlin.InitOnceProperty
import cn.labzen.javafx.stage.LabzenStageContainer
import cn.labzen.javafx.theme.ThemeHandler
import javafx.scene.Parent
import javafx.scene.Scene

@Suppress("MemberVisibilityCanBePrivate")
class ViewWrapper(
  val id: String,
  /**
   * 视图fxml名，不包含'.fxml'扩展名
   */
  val name: String,
  val root: Parent,
  val controller: LabzenView?,
  val width: Double,
  val height: Double
) {
  private val parameter = ViewParameter()
  private val stage = InitOnceProperty<LabzenStageContainer>()
  lateinit var scene: Scene
  private var theme: String? = null

  init {
    controller?.setWrapper(this)
  }

  fun updateParameter(parameters: Map<String, Any>?) {
    parameters?.run {
      parameter.clear()
      parameter.putAll(parameters)
    }
  }

  fun appendParameter(parameters: Map<String, Any>?) {
    parameters?.run {
      parameter.putAll(parameters)
    }
  }

  fun attachTo(stage: LabzenStageContainer) {
    this.stage.set(stage)
  }

  fun sceneCreated() =
    this::scene.isInitialized

  fun createScene() {
    scene = Scene(root, width, height)
    theme = resolveTheme()
    applyTheme()
  }

  fun resolveTheme(): String? =
    when {
      // 如果视图LabzenView指定了明确的皮肤，优先使用
      controller?.theme()?.isNotBlank() == true -> controller.theme()
      // 否则看窗体LabzenStageContainer是否指定了明确的皮肤，如有则用
      // 这里stage未必有值，开发者可以自己加载视图，而未指明视图所依附的窗体
      stage.getOrNull()?.theme()?.isNotBlank() == true -> stage.get().theme()
      // 视图和依附的窗体都未明确定义皮肤，则使用默认皮肤
      else -> null
    }

  internal fun reapplyThemeIfNecessary() {
    val newTheme = resolveTheme()
    if (newTheme != theme) {
      theme = newTheme
      applyTheme()
    }
  }

  private fun applyTheme() {
    if (sceneCreated()) {
      ThemeHandler.applyTheme(scene, theme)
    } else {
      ThemeHandler.applyTheme(root, theme)
    }
  }

  /**
   * 将视图从窗体场景中移除
   */
  fun dispose() {

  }

  /**
   * 销毁视图
   */
  fun destroy() {

  }

}
