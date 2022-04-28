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

  /**
   * 在本视图中的某个容器中，做子视图的跳转
   *
   * @param nodeId 视图中子视图容器的ID，借由[regionalPane]方法决定具体的容器实例
   * @param viewName 跳转的子视图名（fxml文件名/路径，请忽略 '.fxml'），文件名/路径相对于 app.xml
   * 配置文件中的 "app/meta/structure/view"， 例如："user", "user/detail 即可
   * @param parameters 给子视图传递的参数
   */
  @JvmOverloads
  fun go(nodeId: String? = null, viewName: String, parameters: Map<String, Any>? = null) {
    ViewHandler.go(id(), nodeId, viewName, parameters)
  }

  /**
   * 在本视图中的某个容器中，做子视图的跳回
   *
   * @param nodeId 视图中子视图容器的ID，借由[regionalPane]方法决定具体的容器实例
   * @param viewMark 跳转的子视图名，或视图的ID
   * @param parameters 给子视图传递的参数
   */
  @JvmOverloads
  fun back(nodeId: String? = null, viewMark: String? = null, parameters: Map<String, Any>? = null) {
    ViewHandler.back(id(), nodeId, viewMark, viewMark, parameters)
  }

  /**
   * 在本视图中的某个容器中，做子视图的跳回，直接跳回上一个视图
   *
   * @param nodeId 视图中子视图容器的ID，借由[regionalPane]方法决定具体的容器实例
   * @param parameters 给子视图传递的参数
   */
  fun back(nodeId: String? = null, parameters: Map<String, Any>? = null) {
    ViewHandler.back(id(), nodeId, parameters = parameters)
  }

  /**
   * 在本视图中的默认容器中，做子视图的跳回，直接跳回上一个视图
   *
   * @param parameters 给子视图传递的参数
   */
  fun back(parameters: Map<String, Any>) {
    ViewHandler.back(id(), parameters = parameters)
  }

  /**
   * 视图应用的皮肤 ***目录***  路径，无皮肤路径指定，使用窗体的皮肤样式 - (通过 [LabzenStage] 设定)
   */
  open fun theme(): String? = null

  // --------------------------------- about view removal ---------------------------------

  /**
   * 将视图从窗体场景中移除，先调用
   */
  open fun dispose() {
    // default do nothing
  }

  /**
   * 销毁视图，后调用
   */
  open fun destroy() {
    // default do nothing
  }

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
