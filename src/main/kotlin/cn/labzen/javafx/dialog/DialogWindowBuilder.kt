package cn.labzen.javafx.dialog

import cn.labzen.javafx.LabzenJavaFX
import cn.labzen.javafx.LabzenPlatform
import cn.labzen.javafx.stage.LabzenStage
import cn.labzen.javafx.stage.StageHandler
import cn.labzen.logger.kotlin.logger
import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.io.PrintWriter
import java.io.StringWriter
import java.util.function.BiConsumer
import java.util.function.Consumer

class DialogWindowBuilder internal constructor() {

  private val logger = logger { }

  private var parentStageId: String? = null
  private var modal: Boolean = true
  private var icon: String? = null
  private var graphic: String? = null
  private var title: String? = null
  private var message: String? = null
  private var header: String? = null
  private var shutdownWhenClosed: Boolean = false
  private var noCancelButton: Boolean = false
  private var alertType: Alert.AlertType? = null
  private var titleBarHid: Boolean = false
  private var taskBarHid: Boolean = true

  private var expandedContent: Node? = null
  private val buttons = mutableListOf<ButtonType>()
  private val buttonConsumers = mutableMapOf<ButtonType, Consumer<DialogButtonWrapper>>()
  private var callback: BiConsumer<String, ButtonType>? = null

  /**
   * 如果是window并是modal模式框，不指定parent，则置于所有的窗口之上，如果指定，则只置于该窗口之上
   * @param stageId 使用的是通过[StageHandler.createStage]创建的[LabzenStage]接口实现类
   */
  fun parent(stageId: String): DialogWindowBuilder {
    parentStageId = stageId
    return this
  }

  /**
   * 弹框不是模式框，点击其他位置可关闭弹框
   */
  fun dontModal(): DialogWindowBuilder {
    modal = false
    return this
  }

  /**
   * 隐藏标题栏，默认会显示
   *
   * **只window才有效，但任务栏会显示窗体图标（默认无）**
   */
  fun hideTitleBar(): DialogWindowBuilder {
    // 如果这时任务栏还隐藏，需要强制显示出任务栏
    if (taskBarHid) {
      taskBarHid = false
    }

    titleBarHid = true
    return this
  }

  /**
   * 显示任务栏窗体图标，默认不显示
   *
   * **只window才有效**
   */
  fun showTaskBar(): DialogWindowBuilder {
    taskBarHid = false
    return this
  }

  /**
   * 弹框标题
   */
  fun title(value: String): DialogWindowBuilder {
    title = value
    return this
  }

  /**
   * 当弹框关闭时，杀死当前进程
   */
  fun shutdownWhenDialogClosed(): DialogWindowBuilder {
    shutdownWhenClosed = true
    return this
  }

  /**
   * 不要添加默认的取消按钮（默认情况下，自定义的按钮中，
   * 没有[ButtonBar.ButtonData.cancelButton]为true的属性，则会默认添加一个[ButtonType.CANCEL]）
   */
  fun withoutDefaultCancelButton(): DialogWindowBuilder {
    noCancelButton = true
    return this
  }

  /**
   * 弹框（window）的icon图标
   */
  fun icon(location: String): DialogWindowBuilder {
    icon = location
    return this
  }

  /**
   * Alert框标识图片
   */
  fun alertGraphic(location: String): DialogWindowBuilder {
    graphic = location
    return this
  }

  /**
   * 自定义扩展内容
   */
  fun expandContent(content: Node): DialogWindowBuilder {
    this.expandedContent = content
    return this
  }

  /**
   * 自定义按钮，按钮文本必须唯一，按钮出现的顺序与参数一致
   *
   * Alert框会自动添加一个默认按钮，用于关闭框体，按钮可能是右下角的按钮，也可能使用右上角的'X'关闭按钮
   */
  fun customButtons(vararg text: String): DialogWindowBuilder {
    buttons.addAll(text.distinct().map { ButtonType(it) })
    return this
  }

  /**
   * 自定义按钮，按钮文本可重复，但其对应的[ButtonBar.ButtonData]不能相同，否则按重复去除。按钮出现的顺序与位置，取决于[ButtonBar.ButtonData]
   *
   * 如果自定义按钮中不存在[ButtonBar.ButtonData]属性cancelButton为true的话，则默认添加一个用于关闭的按钮；如果存在，程序不会再追加按钮
   */
  fun customButtons(vararg type: ButtonType): DialogWindowBuilder {
    buttons.addAll(type.distinctBy {
      it.text + it.buttonData
    }.toList())
    return this
  }

  /**
   * 自定义按钮，并对其做相应自定义处理；按钮出现的顺序与添加的顺序
   */
  @JvmOverloads
  fun customButton(text: String, consumer: Consumer<DialogButtonWrapper>? = null): DialogWindowBuilder =
    customButton(ButtonType(text), consumer)

  /**
   * 自定义按钮，并对其做相应自定义处理；按钮出现的顺序与位置，取决于[ButtonBar.ButtonData]
   */
  @JvmOverloads
  fun customButton(type: ButtonType, consumer: Consumer<DialogButtonWrapper>? = null): DialogWindowBuilder {
    type.also {
      buttons.add(it)
      if (consumer != null) {
        buttonConsumers[it] = consumer
      }
    }
    return this
  }

  /**
   * 获取按钮node，并对其做相应自定义处理
   *
   * ***建议使用与[buttons]方法相同的参数（text文本 或 [ButtonType]）***
   *
   * 如果添加自定义按钮时，参数使用[ButtonType]；这里使用按钮文本查找，可能会定位到多个button，发生这种情况下，默认发挥第一个找到的button
   */
  fun button(text: String, consumer: Consumer<DialogButtonWrapper>): DialogWindowBuilder {
    buttons.find {
      it.text == text
    }?.apply {
      buttonConsumers[this] = consumer
    }
    return this
  }

  /**
   * 获取按钮node，并对其做相应自定义处理
   *
   * ***建议使用与[buttons]方法相同的参数（text文本 或 [ButtonType]）***
   */
  fun button(type: ButtonType, consumer: Consumer<DialogButtonWrapper>): DialogWindowBuilder {
    buttons.find {
      it.text == type.text && it.buttonData == type.buttonData
    }?.apply {
      buttonConsumers[this] = consumer
    }
    return this
  }

  /**
   * 错误弹框
   */
  @JvmOverloads
  fun error(message: String? = null, throwable: Throwable, header: String? = null): DialogWindowBuilder {
    alertType = ERROR
    this.message = message ?: "The exception stacktrace:"
    this.header = header ?: "Oops!! An Exception occurred."

    val exceptionText = StringWriter().let {
      val pw = PrintWriter(it)
      throwable.printStackTrace(pw)
      it.toString()
    }
    val textArea = TextArea(exceptionText)
    textArea.isEditable = false
    textArea.isWrapText = false
    textArea.maxWidth = Double.MAX_VALUE
    textArea.maxHeight = Double.MAX_VALUE
    expandContent(textArea)
    return this
  }

  /**
   * 错误弹框
   */
  @JvmOverloads
  fun error(message: String, header: String? = null): DialogWindowBuilder {
    alertType = ERROR
    this.message = message
    this.header = header
    return this
  }

  /**
   * 警告弹框
   */
  @JvmOverloads
  fun warning(message: String, header: String? = null): DialogWindowBuilder {
    this.alertType = Alert.AlertType.WARNING
    this.message = message
    this.header = header
    return this
  }

  /**
   * 信息弹框
   */
  @JvmOverloads
  fun information(message: String, header: String? = null): DialogWindowBuilder {
    this.alertType = Alert.AlertType.INFORMATION
    this.message = message
    this.header = header
    return this
  }

  /**
   * 确认弹框
   */
  @JvmOverloads
  fun confirmation(header: String, message: String? = null): DialogWindowBuilder {
    this.alertType = Alert.AlertType.CONFIRMATION
    this.message = message
    this.header = header
    return this
  }

  @JvmOverloads
  fun show(callback: BiConsumer<String, ButtonType>? = null) {
    this.callback = callback
    showWindow()
  }

  private fun showWindow() {
    val window = if (alertType != null) {
      createWindow()
    } else null

    // todo 该方法改为build()，返回window，以DialogWindow的show函数来弹窗，并提供关闭功能，仿照DialogElement
    Platform.runLater {
      window?.showAndWait()?.ifPresent {
        if (shutdownWhenClosed) {
          LabzenJavaFX.terminate()
        }

        callback?.accept(it.text, it)
      }
    }
  }

  private fun createWindow(): DialogWindow {
    val bts = buttons
    if (bts.isNotEmpty() && !noCancelButton) {
      val hasCancelButton = bts.any {
        it.buttonData.isCancelButton
      }
      if (!hasCancelButton) {
        bts.add(ButtonType.CANCEL)
      }
    }

    val aw = DialogWindow(alertType!!, bts).apply {
      // 设置标题、Header头信息、内容信息等文字
      this@apply.title = this@DialogWindowBuilder.title ?: when (alertType) {
        ERROR -> "错误"
        WARNING -> "警告"
        INFORMATION -> "信息"
        CONFIRMATION -> "确认"
        else -> "未知"
      }
      header?.let { headerText = it }
      message?.let { contentText = it }

      // 设置扩展内容
      expandedContent?.let { expandContent(it) }

      // 设置弹窗图标
      icon?.let {
        val image = LabzenPlatform.resource(it)?.let { res ->
          Image(res.toExternalForm())
        }
        image?.let { img ->
          val stage = dialogPane.scene.window as Stage
          stage.icons.add(img)
        } ?: logger.warn("找不到弹框图标：$it")
      }

      // 设置弹窗图示
      this@DialogWindowBuilder.graphic?.let {
        val imageView = LabzenPlatform.resource(it)?.let { res ->
          ImageView(res.toExternalForm()).apply {
            fitWidth = 48.0
            fitHeight = 48.0
          }
        }
        imageView?.let { iv ->
          graphic = iv
        } ?: logger.warn("找不到弹框图示：$it")
      }

      val stageStyle = when {
        taskBarHid && !titleBarHid -> StageStyle.UTILITY
        !taskBarHid && !titleBarHid -> StageStyle.DECORATED
        !taskBarHid && titleBarHid -> StageStyle.UNDECORATED
        else -> {
          logger.warn("逻辑有问题，不可能跑到这儿")
          StageStyle.DECORATED
        }
      }
      // 设置弹窗样式
      initStyle(stageStyle)

      // 设置弹窗模态
      parentStageId?.let {
        val stage = StageHandler.stage(it)?.getStage()
        initOwner(stage)
      }
      when {
        modal && parentStageId != null -> initModality(Modality.WINDOW_MODAL)
        modal && parentStageId == null -> initModality(Modality.APPLICATION_MODAL)
        else -> initModality(Modality.NONE)
      }
    }

    bts.forEach {
      val button = aw.dialogPane.lookupButton(it) as Button
      val consumer = buttonConsumers[it]

      consumer?.accept(DialogButtonWrapper(button))
    }

    return aw
  }
}
