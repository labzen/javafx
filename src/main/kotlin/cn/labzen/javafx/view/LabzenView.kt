package cn.labzen.javafx.view

import cn.labzen.cells.core.kotlin.initOnce
import cn.labzen.cells.core.utils.Randoms
import cn.labzen.javafx.animation.AnimationType
import cn.labzen.javafx.dialog.DialogElement
import cn.labzen.javafx.stage.LabzenStage
import javafx.fxml.Initializable
import javafx.scene.layout.Pane
import java.net.URL
import java.util.*

abstract class LabzenView : Initializable {

  private val id = Randoms.string(10)
  private val wrapper = initOnce<ViewWrapper>()
  internal var wrappedByDialog: DialogElement? = null

  override fun initialize(location: URL?, resources: ResourceBundle?) {
  }

  fun id(): String = id

  internal fun setWrapper(value: ViewWrapper) {
    wrapper.set(value)
  }

  fun wrapper() = wrapper.get()

  open fun regionalPane(id: String?): Pane? = null

  @JvmOverloads
  fun go(nodeId: String? = null, viewName: String, parameters: Map<String, Any>? = null) {
    ViewHandler.go(id(), nodeId, viewName, parameters)
  }

  @JvmOverloads
  fun back(nodeId: String? = null, viewMark: String? = null, parameters: Map<String, Any>? = null) {
    ViewHandler.back(id(), nodeId, viewMark, viewMark, parameters)
  }

  fun back(nodeId: String? = null, parameters: Map<String, Any>? = null) {
    ViewHandler.back(id(), nodeId, parameters = parameters)
  }

  fun back(parameters: Map<String, Any>) {
    ViewHandler.back(id(), parameters = parameters)
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
