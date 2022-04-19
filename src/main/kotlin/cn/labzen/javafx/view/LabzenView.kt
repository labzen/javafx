package cn.labzen.javafx.view

import cn.labzen.cells.core.kotlin.initOnce
import cn.labzen.javafx.animation.AnimationType
import cn.labzen.javafx.dialog.DialogElement
import cn.labzen.javafx.stage.LabzenStage
import javafx.fxml.Initializable
import javafx.scene.SubScene
import java.net.URL
import java.util.*

abstract class LabzenView : Initializable {

  private val id = initOnce<String>()
  private val wrapper = initOnce<ViewWrapper>()
  internal var wrappedByDialog: DialogElement? = null

  override fun initialize(location: URL?, resources: ResourceBundle?) {
  }

  internal fun setId(value: String) {
    id.set(value)
  }

  fun id() = id.get()

  internal fun setWrapper(value: ViewWrapper) {
    wrapper.set(value)
  }

  fun wrapper() = wrapper.get()

  open fun partialViewContainerNode(id: String?): SubScene? = null

  @JvmOverloads
  fun go(nodeId: String? = null, viewName: String, parameters: Map<String, Any>? = null) {
    ViewHandler.go(id(), nodeId, viewName, parameters)
  }

  fun back() {
    ViewHandler.back(id())
  }

  /**
   * 视图应用的皮肤 ***目录***  路径，无皮肤路径指定，使用窗体的皮肤样式 - (通过 [LabzenStage] 设定)
   */
  open fun theme(): String? = null

  // --------------------------------- about dialog element opr ------------------------------------

  /**
   * 关闭
   *
   * @param delay 延迟关闭，单位：毫秒，默认不延迟
   */
  @JvmOverloads
  fun closeDialog(delay: Int? = null) {
    wrappedByDialog?.close()
    wrappedByDialog = null
  }

  @JvmOverloads
  fun animate(type: AnimationType, speed: Double? = null) {
    wrappedByDialog?.run {
      animate(type, speed)
    }
  }
}
