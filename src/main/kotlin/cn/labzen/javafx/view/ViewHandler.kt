package cn.labzen.javafx.view

import cn.labzen.cells.core.kotlin.insureEndsWith
import cn.labzen.cells.core.utils.Randoms
import cn.labzen.javafx.LabzenPlatform
import cn.labzen.javafx.exception.StageViewOperationException
import cn.labzen.logger.kotlin.logger
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.SubScene
import org.springframework.beans.BeansException

object ViewHandler {

  private val logger = logger { }

  private val views = FXCollections.observableHashMap<String, ViewWrapper>()
  private val viewChildrenHistories = mutableMapOf<String, ViewChildrenHistory>()

  internal fun allViews() =
    views

  // todo 可缓存 FXMLLoader ，缓存后使用loader.getRoot()重复使用
  @JvmStatic
  @JvmOverloads
  fun loadView(name: String, parameters: Map<String, Any>? = null): ViewWrapper {
    val viewRoot = LabzenPlatform.container().resourceViewPath
    val viewName = name.insureEndsWith(".fxml")
    val path = "$viewRoot$viewName"

    val resource = LabzenPlatform.resource(path)
    resource ?: throw StageViewOperationException("找不到视图文件：$viewName")
    val loader = FXMLLoader(resource, null, null) {
      val sac = LabzenPlatform.container().springApplicationContext.get()
      try {
        sac.getBean(it)
      } catch (e: BeansException) {
        sac.autowireCapableBeanFactory.createBean(it)
      }
    }

    val id = generateViewId()
    val loadedView: Parent = loader.load()

    val viewController = when (val lc = loader.getController<Any>()) {
      is LabzenView -> lc.also { it.setId(id) }
      null -> null
      else -> {
        logger.warn("强烈建议将视图controller类[${lc.javaClass}]继承LabzenView")
        null
      }
    }

    val prefWidth = loadedView.prefWidth(-1.0)
    val prefHeight = loadedView.prefHeight(-1.0)

    return ViewWrapper(id, name, loadedView, viewController, prefWidth, prefHeight).also {
      it.updateParameter(parameters)
      views[id] = it
    }
  }

  private fun generateViewId() = Randoms.string(10)

  internal fun lookup(viewId: String): ViewWrapper? =
    views[viewId]

  // @return 视图在窗口的视图栈中的唯一ID，可在视图栈中找到准确的位置，当视图被弹出（back）后销毁
  /**
   * 到新的子视图
   *
   * @param primeId 视图（scene view）ID
   * @param nodeId 视图内，需要变更局部子视图的、作为容器概念的节点（继承于[javafx.scene.layout.Region]）名称
   * @param viewName 视图名（fxml文件名/路径，请忽略 '.fxml'），文件名/路径相对于 app.xml 配置文件中的 "app/meta/structure/view"，
   *             例如："user", "user/detail 即可
   * @param parameters 需要传递视图的参数
   */
  @JvmStatic
  @JvmOverloads
  fun go(
    primeId: String,
    nodeId: String? = null,
    viewName: String,
    parameters: Map<String, Any>? = null
  ) {
    Platform.runLater {
      val node = try {
        check(primeId, nodeId)
      } catch (e: Exception) {
        logger.warn(e.message!!)
        return@runLater
      }

      val wrapper = loadView(viewName)
      wrapper.updateParameter(parameters)

      val history = viewChildrenHistories.computeIfAbsent("${primeId}__${nodeId}") { ViewChildrenHistory() }
      history.push(wrapper)

      node.root = wrapper.root
    }
  }

  /**
   * 将子视图回到上一个或某一个视图，[viewId] 与 [viewName] 同时为null时，将显示最后一个视图，类似 back(1)的意思
   *
   * @param primeId 视图（scene view）ID
   * @param nodeId 视图内，需要变更局部子视图的、作为容器概念的节点（继承于[javafx.scene.layout.Region]）名称
   * @param viewId 视图的ID，通过 [go] 方法获取，可准确定位到一个视图在视图栈中的位置；当与 [viewName] 同时出现时，该参数优先被使用
   * @param viewName 视图名（fxml文件名/路径，请忽略 '.fxml'），文件名/路径相对于 app.xml 配置文件中的 "app/meta/structure/view"，
   *             例如："user", "user/detail 即可；如在窗口的视图栈中存在多个相同的view，则默认使用视图栈中最新的那个
   * @param parameters 需要传递视图的参数
   */
  @JvmStatic
  @JvmOverloads
  fun back(
    primeId: String,
    nodeId: String? = null,
    viewId: String? = null,
    viewName: String? = null,
    parameters: Map<String, Any>? = null
  ) {
    Platform.runLater {
      val subScene = try {
        check(primeId, nodeId)
      } catch (e: Exception) {
        logger.warn(e.message!!)
        return@runLater
      }

      val history = viewChildrenHistories["${primeId}__${nodeId}"]!!
      val viewMeta = history.searchAndPop(viewId, viewName, parameters)
      viewMeta?.apply {
        subScene.root = root
      } ?: logger.warn {
        "无法执行子视图 back() for - prime_view_id: $primeId, node_container: $nodeId, view_id: $viewId, view_name: $viewName"
      }
    }
  }

  /**
   * 替换一个已存在的视图，如不存在，则操作不生效；操作完成后，视图栈的size不改变，未涉及到的其他视图原位置不变
   *
   * @param primeId 视图（scene view）ID
   * @param node 视图内，需要变更局部子视图的、作为容器概念的节点（继承于[javafx.scene.layout.Region]）名称
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
    primeId: String? = null,
    node: String? = null,
    viewId: String? = null,
    viewName: String,
    parameters: Map<String, Any>? = null
  ): String {
    return ""
  }

  private fun check(primeId: String, nodeId: String? = null): SubScene {
    val primeView = views[primeId] ?: throw IllegalArgumentException("找不到可变更的主视图 prime_view_id: $primeId")

    val controller = primeView.controller
    if (controller !is LabzenView) {
      throw IllegalArgumentException("在主视图中实现局部视图变更，需要继承类 cn.labzen.javafx.view.LabzenView")
    }

    return controller.partialViewContainerNode(nodeId) ?: throw IllegalArgumentException("找不到可做局部视图变更的容器节点")
  }
}
