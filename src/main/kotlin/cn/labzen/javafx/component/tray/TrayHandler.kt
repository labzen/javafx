package cn.labzen.javafx.component.tray

import cn.labzen.javafx.LabzenPlatform
import cn.labzen.javafx.css.MonitoredTargetRegistry
import cn.labzen.javafx.exception.ApplicationBootException
import cn.labzen.javafx.exception.ApplicationException
import cn.labzen.logger.kotlin.logger
import javafx.application.Platform
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.scene.Scene
import javafx.scene.control.ContextMenu
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import java.awt.HeadlessException
import java.awt.Image
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.event.MouseAdapter
import java.lang.reflect.Method
import javax.imageio.ImageIO
import java.awt.event.MouseEvent as AwtMouseEvent

object TrayHandler {

  private val logger = logger { }

  private val DEFAULT_TRAY_ICON: Image by lazy {
    ImageIO.read(this.javaClass.getResource("/icon/default_32x32.png"))
  }

  private lateinit var clsn: String
  private lateinit var cls: Class<TrayMenuController>
  private lateinit var handler: TrayMenuController
  private lateinit var tray: SystemTray
  private lateinit var icon: Image
  private lateinit var contextMenu: ContextMenu
  private lateinit var contextMenuCss: List<String>
  private lateinit var contextMenuHelper: Stage
  private lateinit var handlerMethods: Map<String, Method>
  private lateinit var handlerMenuItemDefines: MenuItemParent

  private val menuDisabledListener = ListChangeListener<Any>() {

  }
  private val menuHiddenListener = ListChangeListener<Any>() {

  }

  internal fun initialize(handlerClassName: String, iconPath: String?, css: String?) {
    // 托盘菜单处理类
    val c = try {
      Class.forName(handlerClassName)
    } catch (e: Exception) {
      throw ApplicationBootException(e, "找不到托盘菜单处理类：$handlerClassName")
    }
    cls = if (AbstractTrayMenuController::class.java.isAssignableFrom(c)) {
      @Suppress("UNCHECKED_CAST")
      c as Class<TrayMenuController>
    } else throw ApplicationBootException("托盘菜单处理类必须继承AbstractTrayMenuController接口：$handlerClassName")

    // 托盘图标
    iconPath?.also {
      val resource = this.javaClass.getResource(it)
      icon = ImageIO.read(resource)
    }

    contextMenuCss = css?.let {
      css.split(',').mapNotNull {
        this.javaClass.getResource(it)?.toExternalForm()
      }
    } ?: run {
      listOf(this.javaClass.getResource("/css/menu.css")!!.toExternalForm())
    }

    // 兼容：标记JVM运行平台为非 Headless 模式，避免获取系统托盘异常
    System.setProperty("java.awt.headless", "false")

    try {
      tray = SystemTray.getSystemTray()
    } catch (e: SecurityException) {
      throw ApplicationException(e, "当前系统未授权使用托盘菜单功能")
    } catch (e: UnsupportedOperationException) {
      throw ApplicationException(e, "当前系统不支持系统托盘功能")
    } catch (e: HeadlessException) {
      throw ApplicationException(e, "当前系统无可支持显示器、键盘和鼠标（工作在 Headless 模式下）")
    }

    // 解析托盘菜单处理类
    parseHandler()

    clsn = handlerClassName
  }

  /**
   * javafx的弹出窗体（PopupWindow, ContextMenu继承于此），必须有一个owner，这里给弄个看不见的糊弄糊弄
   */
  private fun prepareInvisibleContextMenuHelperStage() {
    if (this::contextMenuHelper.isInitialized) {
      return
    }

    contextMenuHelper = Stage().apply {
      initStyle(StageStyle.UTILITY)

      this.scene = Scene(Pane(), 1.0, 1.0).also {
        it.fill = Color.TRANSPARENT

        it.stylesheets.addAll(contextMenuCss)
      }

      // 如要调试托盘菜单的样式，可修改下面方法的可见度为internal
      MonitoredTargetRegistry.registerScene(scene)

      x = Double.MAX_VALUE
      y = Double.MAX_VALUE

      this.xProperty().addListener { _, _, n ->
        if (n != Double.MAX_VALUE) {
          x = Double.MAX_VALUE
        }
      }
      this.yProperty().addListener { _, _, n ->
        if (n != Double.MAX_VALUE) {
          y = Double.MAX_VALUE
        }
      }
      show()
    }
  }

  private fun parseHandler() {
    val sac = LabzenPlatform.container().springApplicationContext.get()
    handler = try {
      sac.getBean(cls)
    } catch (e: NoSuchBeanDefinitionException) {
      cls.getConstructor().newInstance()
    } catch (e: Exception) {
      throw ApplicationBootException(e, "无法获取（或创建）托盘处理类实例：$cls")
    }

    val disabledItemsField = cls.fields.find { it.isAnnotationPresent(TrayMenuDisabledItems::class.java) }
    disabledItemsField?.also {
      if (it.type == ObservableList::class.java) {
        it.isAccessible = true
        val list = it.get(handler) as ObservableList<*>
        list.addListener(menuDisabledListener)
      } else {
        logger.warn("托盘处理类中，被TrayMenuDisabledItems注解修饰的属性类型必须是：ObservableList<String>")
      }
    }

    val hiddenItemsField = cls.fields.find { it.isAnnotationPresent(TrayMenuHiddenItems::class.java) }
    hiddenItemsField?.also {
      if (it.type == ObservableList::class.java) {
        it.isAccessible = true
        val list = it.get(handler) as ObservableList<*>
        list.addListener(menuHiddenListener)
      } else {
        logger.warn("托盘处理类中，被TrayMenuHiddenItems注解修饰的属性类型必须是：ObservableList<String>")
      }
    }

    // 将注解拆出来 List<Pair<TrayItem!, Method!>>
    val methods = cls.methods.mapNotNull {
      if (it.isAnnotationPresent(TrayItem::class.java)) {
        Pair(it.getAnnotation(TrayItem::class.java), it)
      } else null
    }
    // 按注解TrayItem.name作为key，存储对应method
    handlerMethods = methods.associate {
      Pair(it.first.name, it.second)
    }

    handlerMenuItemDefines = MenuItemParent.ROOT
    parseHandlerMenuItemDefinesPerLevel(handlerMenuItemDefines, methods.map { it.first }, 0, "")
  }

  private fun parseHandlerMenuItemDefinesPerLevel(
    mip: MenuItemParent,
    itemAnnotations: List<TrayItem>,
    level: Int,
    prefix: String
  ) {
    val itemsInThisLevel = itemAnnotations.filter {
      val cn = it.name.trim('/')
      val count = cn.count { c -> c == '/' }
      it.name.startsWith(prefix) && count == level
    }.sortedBy {
      // 先混合分组与组内顺序进行排序
      it.group * 10 + it.order
    }.reversed()

    if (itemsInThisLevel.isEmpty()) {
      return
    }

    for (item in itemsInThisLevel) {
      // 处理子菜单
      if (item.name.endsWith('/')) {
        val sub = MenuItemParent(item.name, item.text, item.group).also { mip.items.add(it) }
        parseHandlerMenuItemDefinesPerLevel(sub, itemAnnotations, level + 1, item.name)
      } else {
        val method = handlerMethods[item.name]!!
        mip.items.add(MenuItemElement(item.name, item.text, item.group, item.order, item.selectable, method))
      }
    }
  }

  fun create(tooltip: String) {
    if (!this::tray.isInitialized || !this::handler.isInitialized) {
      return
    }

    val icon = if (this::icon.isInitialized) {
      this.icon
    } else {
      DEFAULT_TRAY_ICON
    }

    val trayIcon = TrayIcon(icon, tooltip).also {
      it.isImageAutoSize = true
    }

    trayIcon.addMouseListener(object : MouseAdapter() {
      override fun mouseClicked(e: AwtMouseEvent) {
        mouseEvent(e)
      }
    })

    tray.add(trayIcon)
  }

  private fun mouseEvent(e: AwtMouseEvent) {
    when (e.button) {
      AwtMouseEvent.BUTTON3 -> {
        Platform.runLater {
          showMenu(e.xOnScreen.toDouble(), e.yOnScreen.toDouble())
        }
        handler.secondaryClicked()
      }
      AwtMouseEvent.BUTTON1 -> {
        if (e.clickCount == 2) {
          handler.doubleClicked()
        } else {
          handler.clicked()
        }
      }
    }
  }

  private fun showMenu(x: Double, y: Double) {
    prepareInvisibleContextMenuHelperStage()
    contextMenu = handler.createMenu() ?: ContextMenu()

    createMenuItems(contextMenu)

    contextMenu.show(contextMenuHelper, x, y)
    contextMenuHelper.requestFocus()
  }

  private fun createMenuItems(contextMenu: ContextMenu) {
    // todo 加缓存机制，如果菜单项有变化，重新缓存
    buildMenuItems(contextMenu.items, handlerMenuItemDefines.items)
  }

  private fun buildMenuItems(contextMenuItems: ObservableList<MenuItem>, handlerMenuItems: ArrayList<MenuItemMeta>) {
    var latestGroup = handlerMenuItems.first().group
    for (hmi in handlerMenuItems) {
      if (latestGroup != hmi.group) {
        contextMenuItems.add(SeparatorMenuItem())
        latestGroup = hmi.group
      }

      if (hmi is MenuItemElement) {
        val mi = if (hmi.selectable) {
          buildSelectableMenuItem(hmi)
        } else {
          buildNormalMenuItem(hmi)
        }

        contextMenuItems.add(mi)
      } else {
        val hmp = hmi as MenuItemParent
        val subItems = Menu(hmi.text)
        buildMenuItems(subItems.items, hmp.items)
        contextMenuItems.add(subItems)
      }
    }
  }

  private fun buildSelectableMenuItem(hmi: MenuItemElement): MenuItem {
    val functionInvoker: (selected: Boolean) -> Unit = {
      if (hmi.rightSelectableMethod) {
        hmi.method.invoke(handler, it)
      }
    }

    val defaultItem = SelectableMenuItem(hmi.text, false, functionInvoker)
    return handler.createMenuItem(hmi.name, hmi.text, true, defaultItem)
  }

  private fun buildNormalMenuItem(hmi: MenuItemElement): MenuItem {
    val functionInvoker: () -> Unit = {
      hmi.method.invoke(handler)
    }

    return handler.createMenuItem(hmi.name, hmi.text, false, MenuItem(hmi.text)).also {
      it.addEventHandler(ActionEvent.ACTION) { functionInvoker() }
    }
  }


}
