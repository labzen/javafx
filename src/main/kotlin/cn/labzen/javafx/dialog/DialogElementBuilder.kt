package cn.labzen.javafx.dialog

import cn.labzen.javafx.animation.AnimationType
import cn.labzen.javafx.dialog.DialogElementEvent.Companion.CLOSED_TYPE
import cn.labzen.javafx.dialog.DialogElementEvent.Companion.OPENED_TYPE
import cn.labzen.javafx.exception.DialogException
import cn.labzen.javafx.view.LabzenView
import cn.labzen.javafx.view.ViewHandler
import cn.labzen.javafx.view.ViewWrapper
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color

class DialogElementBuilder internal constructor() {

  private var containerControllerInterface: DialogElementContainer? = null
  private var contentView: LabzenView? = null
  private var contentNode: Node? = null
  private var containerPane: StackPane? = null
  private var showAnimation: AnimationType = AnimationType.NONE
  private var showAnimationSpeed: Double = 1.0
  private var showAnimationDelay: Double = 0.0
  private var closeAnimation: AnimationType = AnimationType.NONE
  private var closeAnimationSpeed: Double = 1.0
  private var closeAnimationDelay: Double = 0.0
  private var overlayClose: Boolean = false
  private var overlayBackgroundColor: Color = Color.BLACK
  private var overlayBackgroundOpacity: Double = 0.3
  private var position: Pos = Pos.CENTER
  private var margin: Insets? = null

  private fun findStackPaneFromControllerContainer(
    container: DialogElementContainer,
    name: String? = null
  ): StackPane {
    val paneFields = container.javaClass.declaredFields
    val paneField = when (paneFields.size) {
      0 -> throw DialogException("在${container.javaClass}中无法找到合适的StackPane作为弹框容器")
      1 -> paneFields[0]
      else -> {
        paneFields.find {
          it.type == StackPane::class.java && (name == null || it.name == name)
        } ?: throw DialogException("在${container.javaClass}中无法找到属性名为${name}的StackPane作为弹框容器")
      }
    }

    paneField.isAccessible = true
    val pane = paneField.get(container)
      ?: throw DialogException("在${container.javaClass}中属性名为${name}的StackPane容器实例为null")
    return pane as StackPane
  }

  private fun findStackPaneFromViewNode(node: Node): StackPane? =
    when (node) {
      is StackPane -> node
      is Parent -> node.childrenUnmodifiable.map { findStackPaneFromViewNode(it) }.firstOrNull()
      else -> null
    }

  /**
   * 设置弹框容器，如果视图对应的controller实现了[DialogElementContainer]接口，可监听弹框的显示、关闭等事件
   *
   * 相对适合容器视图中仅存在一个[StackPane]的情况（比较常见的场景）
   *
   * @param viewId 视图ID，程序会自动在视图节点下找寻第一个[StackPane]节点作为弹窗的容器（蒙层遮挡的区域）
   * @throws DialogException 找不到视图，或明确的StackPane作为弹框容器
   */
  @Throws(DialogException::class)
  fun container(viewId: String): DialogElementBuilder {
    val view = ViewHandler.lookup(viewId) ?: throw DialogException("未找到ID为[$viewId]的视图信息")

    // 找到容器controller
    containerControllerInterface = if (view.controller != null && view.controller is DialogElementContainer) {
      view.controller
    } else null

    // 先从controller里查找
    var containerPane = containerControllerInterface?.let {
      try {
        findStackPaneFromControllerContainer(it)
      } catch (e: Exception) {
        null
      }
    }

    // 找不到，再从视图node中查找
    containerPane = containerPane ?: run {
      findStackPaneFromViewNode(view.root)
    }

    containerPane ?: throw DialogException("无法在视图相关的资源中找到合适的StackPane作为弹框容器")
    container(containerPane)

    return this
  }

  /**
   * 设置弹框容器，可监听弹框的显示、关闭等事件
   *
   * 相对可更灵活的适应一个容器视图中存在多个[StackPane]的场景
   *
   * @param containerInterface 实现了[DialogElementContainer]接口的视图controller，程序会自动在controller中找寻一个[StackPane]节点作为弹窗的容器（蒙层遮挡的区域）
   * @param name 如果存在多个[StackPane]，需要指定容器的属性名，不指定会自动使用找到的第一个[StackPane]；如果只有一个，该参数可不需要
   * @throws DialogException 找不到明确的StackPane作为弹框容器
   */
  @Throws(DialogException::class)
  @JvmOverloads
  fun container(containerInterface: DialogElementContainer, name: String? = null): DialogElementBuilder {
    containerControllerInterface = containerInterface

    val pane = findStackPaneFromControllerContainer(containerInterface, name)
    container(pane)
    return this
  }

  /**
   * 设置弹框容器，无法监听弹框的显示、关闭等事件
   *
   * @param containerPane 弹窗的容器（蒙层遮挡的区域）
   */
  fun container(containerPane: StackPane): DialogElementBuilder {
    this.containerPane = containerPane
    return this
  }

  /**
   * 设置弹框显示内容，如果视图对应的controller实现了[LabzenView]接口，可提供便捷的操作弹框方法
   *
   * @param viewId 视图ID，程序会自动使用对应视图根节点作为显示内容
   */
  fun content(viewId: String): DialogElementBuilder {
    val view = ViewHandler.lookup(viewId) ?: throw DialogException("未找到ID为[$viewId]的视图信息")
    return content(view)
  }

  /**
   * 设置弹框显示内容，可提供便捷的操作弹框方法
   *
   * @param wrapper [ViewWrapper]视图资源包裹
   */
  @Throws(DialogException::class)
  fun content(wrapper: ViewWrapper): DialogElementBuilder {
    contentView = wrapper.controller
    return content(wrapper.root)
  }

  /**
   * 设置弹框显示内容，可提供便捷的操作弹框方法
   *
   * @param contentView 继承[LabzenView]的视图controller
   * @throws DialogException 视图controller类未继承自[LabzenView]
   */
  @Throws(DialogException::class)
  fun content(contentView: LabzenView): DialogElementBuilder {
    this.contentView = contentView
    val wrapper = contentView.wrapper()
    return content(wrapper.root)
  }

  /**
   * 设置弹框显示内容，无法提供便捷的操作弹框方法
   *
   * @param node 内容节点
   */
  private fun content(node: Node): DialogElementBuilder {
    contentNode = node
    return this
  }

  /**
   * 使用动画显示弹框，默认无动画效果
   *
   * @param animation 动画效果 [AnimationType]
   * @param speed 动画速度，正常速度 1.0（默认），小于1.0慢速动画，大于1.0加速动画，小于0反向动画效果
   * @param delay 延迟多久开始动画效果，单位（毫秒），默认0
   */
  @JvmOverloads
  fun showWithAnimation(animation: AnimationType, speed: Double? = null, delay: Int? = null): DialogElementBuilder {
    showAnimation = animation
    showAnimationSpeed = speed ?: 1.0
    showAnimationDelay = delay?.toDouble() ?: 0.0
    return this
  }

  /**
   * 使用动画关闭弹框，默认无动画效果
   *
   * @param animation 动画效果 [AnimationType]
   * @param speed 动画速度，正常速度 1.0（默认），小于1.0慢速动画，大于1.0加速动画，小于0反向动画效果
   * @param delay 延迟多久开始动画效果，单位（毫秒），默认0
   */
  @JvmOverloads
  fun closeWithAnimation(animation: AnimationType, speed: Double? = null, delay: Int? = null): DialogElementBuilder {
    closeAnimation = animation
    closeAnimationSpeed = speed ?: 1.0
    closeAnimationDelay = delay?.toDouble() ?: 0.0
    return this
  }

  /**
   * 点击弹框范围之外的蒙层关闭弹框，默认不关闭
   */
  fun closeByOverlay(): DialogElementBuilder {
    overlayClose = true
    return this
  }

  /**
   * 蒙层背景
   * @param color 颜色，默认 黑色
   * @param opacity 蒙层透明度，默认 0.3
   */
  @JvmOverloads
  fun overlayColor(color: Color, opacity: Double? = null): DialogElementBuilder {
    overlayBackgroundColor = color
    overlayBackgroundOpacity = opacity ?: color.opacity
    return this
  }

  /**
   * 弹窗位置
   */
  @JvmOverloads
  fun position(pos: Pos, margin: Insets? = null): DialogElementBuilder {
    this.position = pos
    this.margin = margin
    return this
  }

  /**
   * 弹窗位置
   */
  fun position(margin: Insets): DialogElementBuilder {
    this.margin = margin
    return this
  }

  fun build(): DialogElement {
    containerPane ?: throw DialogException("必须指定弹框的容器节点，请调用 container() 方法")
    contentNode ?: throw DialogException("必须明确弹框的显示内容节点，请调用 content() 方法")

    return DialogElement(containerPane!!, contentNode!!, overlayClose).apply {
      // 背景、动效、显示位置等
      background(overlayBackgroundColor, overlayBackgroundOpacity)
      if (showAnimation != AnimationType.NONE) {
        showAnimationConfig = DialogElement.AnimationConfig(showAnimation, showAnimationSpeed, showAnimationDelay)
      }
      if (closeAnimation != AnimationType.NONE) {
        closeAnimationConfig = DialogElement.AnimationConfig(closeAnimation, closeAnimationSpeed, closeAnimationDelay)
      }
      location(position, margin ?: Insets(.0, .0, .0, .0))

      // 检查弹框内容是否已被引用，并指向引用
      contentView?.also {
        if (it.wrappedByDialog != null) {
          throw DialogException("弹框内容（视图实例）不能被应用于多个弹框中")
        }
        it.wrappedByDialog = this
      }

      // 注册事件
      addEventHandler(OPENED_TYPE) {
        containerControllerInterface?.run {
          dialogOpened()
        }
      }
      addEventHandler(CLOSED_TYPE) {
        containerControllerInterface?.run {
          dialogClosed()
        }
      }
    }
  }

}
