package cn.labzen.javafx.view

import cn.labzen.cells.core.kotlin.insureEndsWith
import cn.labzen.javafx.LabzenPlatform
import cn.labzen.javafx.exception.StageViewOperationException
import cn.labzen.logger.kotlin.logger
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.layout.Pane
import org.springframework.beans.BeansException

object ViewHandler {

  private val logger = logger { }

  private val viewsById = FXCollections.observableHashMap<String, ViewWrapper>()
  private val viewsByName = FXCollections.observableHashMap<String, ViewWrapper>()
  private val viewChildrenHistories = mutableMapOf<String, ViewChildrenHistory>()

  internal fun allViews() =
    viewsById

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

    val loadedView: Parent = loader.load()
    val controller = loader.getController<Any>()
    val vc = controller?.let {
      if (controller !is LabzenView) {
        throw StageViewOperationException("视图[$name]的controller类需要继承LabzenView")
      }
      it as LabzenView
    }

    val prefWidth = loadedView.prefWidth(-1.0)
    val prefHeight = loadedView.prefHeight(-1.0)

    val vcId = vc?.id() ?: name
    return ViewWrapper(vcId, name, loadedView, vc, prefWidth, prefHeight).also {
      it.updateParameter(parameters)
      viewsById[vcId] = it
      viewsByName[name] = it

      it.initialized()
    }
  }

  internal fun lookup(primeIdOrName: String): ViewWrapper? =
    viewsById[primeIdOrName] ?: viewsByName[primeIdOrName]

  // @return 视图在窗口的视图栈中的唯一ID，可在视图栈中找到准确的位置，当视图被弹出（back）后销毁
  /**
   * 到新的子视图
   *
   * @param primeIdOrName 视图（scene view）ID，或视图名
   * @param nodeId 视图内，需要变更局部子视图的、作为容器概念的节点（继承于[javafx.scene.layout.Pane]）名称
   * @param viewName 视图名（fxml文件名/路径，请忽略 '.fxml'），文件名/路径相对于 app.xml 配置文件中的 "app/meta/structure/view"，
   *             例如："user", "user/detail 即可
   * @param parameters 需要传递视图的参数
   */
  @JvmStatic
  @JvmOverloads
  fun go(
    primeIdOrName: String,
    nodeId: String? = null,
    viewName: String,
    parameters: Map<String, Any>? = null
  ) {
    Platform.runLater {
      val node = try {
        check(primeIdOrName, nodeId)
      } catch (e: Exception) {
        logger.warn(e.message!!)
        return@runLater
      }

      val wrapper = loadView(viewName, parameters)
      // wrapper.updateParameter(parameters)

      val primeWrapper = lookup(primeIdOrName)!!
      val key = "${primeWrapper.id}__${nodeId ?: "_nid"}"
      val history = viewChildrenHistories.computeIfAbsent(key) { ViewChildrenHistory() }
      val currentWrapper = history.peek()
      history.push(wrapper)

      currentWrapper?.controller?.dispose()
      node.children.clear()
      node.children.add(wrapper.root)
    }
  }

  /**
   * 将子视图回到上一个或某一个视图，[viewId] 与 [viewName] 同时为null时，将显示最后一个视图，类似 back(1)的意思
   *
   * @param primeIdOrName 视图（scene view）ID，或视图名
   * @param nodeId 视图内，需要变更局部子视图的、作为容器概念的节点（继承于[javafx.scene.layout.Pane]）名称
   * @param viewId 视图的ID，通过 [go] 方法获取，可准确定位到一个视图在视图栈中的位置；当与 [viewName] 同时出现时，该参数优先被使用
   * @param viewName 视图名（fxml文件名/路径，请忽略 '.fxml'），文件名/路径相对于 app.xml 配置文件中的 "app/meta/structure/view"，
   *             例如："user", "user/detail 即可；如在窗口的视图栈中存在多个相同的view，则默认使用视图栈中最新的那个
   * @param parameters 需要传递视图的参数
   */
  @JvmStatic
  @JvmOverloads
  fun back(
    primeIdOrName: String,
    nodeId: String? = null,
    viewId: String? = null,
    viewName: String? = null,
    parameters: Map<String, Any>? = null
  ) {
    Platform.runLater {
      val node = try {
        check(primeIdOrName, nodeId)
      } catch (e: Exception) {
        logger.warn(e.message!!)
        return@runLater
      }

      val primeWrapper = lookup(primeIdOrName)!!
      val key = "${primeWrapper.id}__${nodeId ?: "_nid"}"
      val history = viewChildrenHistories[key]!!
      val foundWrapper = history.search(viewId, viewName)

      val poppedWrappers = if (foundWrapper == null) {
        history.pop()?.let { listOf(it) } ?: emptyList()
      } else {
        history.popUntil(foundWrapper)
      }

      destroyView(poppedWrappers)

      val resumeWrapper = history.peek()
      if (resumeWrapper == null) {
        logger.debug() {
          "找不到子视图 back() for - prime_view: $primeIdOrName, node: $nodeId, sub_view_id: $viewId, sub_view_name: $viewName"
        }
        return@runLater
      }

      // val wrapper = history.searchAndPop(viewId, viewName, parameters)
      resumeWrapper.updateParameter(parameters)
      node.children.clear()
      node.children.add(resumeWrapper.root)
    }
  }

  /**
   * 销毁弹出的视图
   */
  private fun destroyView(viewWrappers: List<ViewWrapper>) {
    Platform.runLater {
      for (wrapper in viewWrappers) {
        wrapper.controller?.apply {
          dispose()
          destroy()
        }
      }
    }
  }

  // @return 视图替换掉原 [viewId] 的视图后，在窗口的视图栈中的唯一ID，位置与原 [viewId] 的视图位置一致，
  /**
   * 替换当前的视图，操作完成后，视图栈的size不改变，其他视图原位置不变，原视图被弹出（back）后销毁
   *
   * @param primeIdOrName 视图（scene view）ID，或视图名
   * @param nodeId 视图内，需要变更局部子视图的、作为容器概念的节点（继承于[javafx.scene.layout.Region]）名称
   * @param viewName 视图名（fxml文件名/路径，请忽略 '.fxml'），文件名/路径相对于 app.xml 配置文件中的 "app/meta/structure/view"，
   *             例如："user", "user/detail 即可
   * @param  parameters 需要传递视图的参数
   */
  @JvmStatic
  @JvmOverloads
  fun replace(
    primeIdOrName: String,
    nodeId: String? = null,
    viewName: String,
    parameters: Map<String, Any>? = null
  ) {
    Platform.runLater {
      val node = try {
        check(primeIdOrName, nodeId)
      } catch (e: Exception) {
        logger.warn(e.message!!)
        return@runLater
      }

      val wrapper = loadView(viewName, parameters)
      // wrapper.updateParameter(parameters)

      val primeWrapper = lookup(primeIdOrName)!!
      val key = "${primeWrapper.id}__${nodeId ?: "_nid"}"
      val history = viewChildrenHistories.computeIfAbsent(key) { ViewChildrenHistory() }
      val currentWrapper = history.pop()
      history.push(wrapper)

      currentWrapper?.run {
        destroyView(listOf(this))
      }

      node.children.clear()
      node.children.add(wrapper.root)
    }
  }

  private fun check(primeViewMark: String, nodeId: String?): Pane {
    val primeView = viewsById[primeViewMark] ?: viewsByName[primeViewMark]
    ?: throw IllegalArgumentException("找不到可变更的主视图:$primeViewMark")

    val controller = primeView.controller
    if (controller !is LabzenView) {
      throw IllegalArgumentException("在主视图中实现局部视图变更，需要继承类 cn.labzen.javafx.view.LabzenView")
    }

    return controller.regionalPane(nodeId) ?: throw IllegalArgumentException("找不到可做局部视图变更的容器节点")
  }

}
