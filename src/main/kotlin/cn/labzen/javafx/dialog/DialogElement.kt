package cn.labzen.javafx.dialog

import cn.labzen.javafx.animation.AnimationType
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.event.Event
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.util.Duration

/**
 * 在视图界面中，以蒙层方式弹出的弹框，类似网页的弹框
 */
class DialogElement internal constructor(
  internal val container: StackPane,
  private val content: Node,
  overlayClose: Boolean = true
) : StackPane() {

  internal var showAnimationConfig: AnimationConfig? = null
  internal var closeAnimationConfig: AnimationConfig? = null

  private val contentHolder: StackPane
  private lateinit var tempContent: List<Node>


  init {
    // 本StackPane充当mask背景
    isVisible = false

    // contentHolder才是真正弹窗显示内容的容器
    contentHolder = StackPane().also {
      it.background = Background(BackgroundFill(Color.WHITE, CornerRadii(2.0), null))
      it.isPickOnBounds = false
      // 确保堆栈窗格的大小永远不会超过它的首选大小
      it.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE)
      // prevent propagating the events to overlay pane
      it.addEventHandler(MouseEvent.ANY) { e -> e.consume() }

      it.children.setAll(content)
    }

    // 这里没必要使用with，这样写只是为了容易辨认代码
    with(this) {
      children.add(contentHolder)
      setAlignment(contentHolder, Pos.CENTER)
      background = Background(BackgroundFill(Color.rgb(0, 0, 0, 0.3), null, null))
      // close the dialog if clicked on the overlay pane
      if (overlayClose) {
        addEventHandler(MouseEvent.MOUSE_PRESSED) { close() }
      }
    }
  }

  internal fun background(color: Color, opacity: Double) {
    val bc = with(color) {
      Color.color(red, green, blue, opacity)
    }
    background = Background(BackgroundFill(bc, null, null))
  }

  internal fun location(pos: Pos, margins: Insets) {
    setAlignment(contentHolder, pos)
    setMargin(contentHolder, margins)
  }

  /**
   * 弹出
   *
   * @param delay 延迟弹出，单位：毫秒，默认不延迟
   */
  @JvmOverloads
  fun show(delay: Int? = null) {
    delay?.run {
      val frame = KeyFrame(Duration.millis(delay.toDouble()), { internalShow() })
      Platform.runLater(Timeline(frame)::play)
    } ?: run {
      Platform.runLater(this::internalShow)
    }
  }

  private fun internalShow() {
    tempContent = container.children.toList()
    tempContent.forEach {
      changFocusTraversable(it, false)
    }

    container.children.add(this)
    isVisible = true
    // 使弹框内的第一个节点获得焦点
    if (content is Parent) {
      content.childrenUnmodifiable.first {
        it !is Pane && it !is Group
      }.requestFocus()
    } else {
      content.requestFocus()
    }

    showAnimationConfig?.run {
      contentHolder.opacity = 0.0
      val dc = type.cls.getDeclaredConstructor(Node::class.java)
      val animation = dc.newInstance(contentHolder)
      animation.setSpeed(speed).setDelay(Duration.millis(delay))
      animation.setOnFinished {
        Event.fireEvent(this@DialogElement, DialogElementEvent.OPENED_EVENT)
      }
      animation.play()
    } ?: run {
      Event.fireEvent(this, DialogElementEvent.OPENED_EVENT)
    }
  }

  /**
   * 弹框显示前，移除掉原界面的tab键导航功能。当弹框关闭时恢复
   */
  private fun changFocusTraversable(node: Node, enable: Boolean) {
    if (node !is Pane && node is Parent && node !is Group) {
      node.focusTraversableProperty().set(enable)
    }

    if (node is Parent) {
      node.childrenUnmodifiable.forEach {
        changFocusTraversable(it, enable)
      }
    }
  }

  /**
   * 关闭
   *
   * @param delay 延迟关闭，单位：毫秒，默认不延迟
   */
  @JvmOverloads
  fun close(delay: Int? = null) {
    delay?.run {
      val frame = KeyFrame(Duration.millis(delay.toDouble()), { internalClose() })
      Platform.runLater(Timeline(frame)::play)
    } ?: run {
      Platform.runLater(this::internalClose)
    }
  }

  private fun internalClose() {
    closeAnimationConfig?.run {
      val dc = type.cls.getDeclaredConstructor(Node::class.java)
      val animation = dc.newInstance(contentHolder)
      animation.setSpeed(speed).setDelay(Duration.millis(delay))
      animation.setOnFinished {
        closeDialog()
      }
      animation.play()
    } ?: run {
      closeDialog()
    }
  }

  private fun closeDialog() {
    resetProperties()
    container.children.remove(this)
    Event.fireEvent(this, DialogElementEvent.CLOSED_EVENT)
  }

  private fun resetProperties() {
    opacity = 0.0
    this.isVisible = false

    tempContent.forEach {
      changFocusTraversable(it, true)
    }
  }

  /**
   * 在弹框显示后，随时的动效展示，无回调
   */
  @JvmOverloads
  fun animate(type: AnimationType, speed: Double? = null) {
    if (AnimationType.NONE == type) {
      return
    }

    val dc = type.cls.getDeclaredConstructor(Node::class.java)
    val animation = dc.newInstance(contentHolder)
    animation.setSpeed(speed ?: 1.0)
    animation.play()
  }

  internal data class AnimationConfig(val type: AnimationType, val speed: Double, val delay: Double)
}
