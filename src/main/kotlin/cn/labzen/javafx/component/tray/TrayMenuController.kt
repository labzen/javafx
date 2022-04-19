package cn.labzen.javafx.component.tray

import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem

/**
 * 需要一个空构造函数
 */
interface TrayMenuController {

  /**
   * 托盘图标被左建点击，默认单击恢复窗口显示
   */
  fun clicked()

  /**
   * 托盘图标被左建双击（该方法被触发前，会先触发[clicked]）
   */
  fun doubleClicked()

  /**
   * 托盘图标被右键点击，默认会先弹出菜单，然后触发该方法
   */
  fun secondaryClicked()

  /**
   * 可自定义 [ContextMenu]，如返回空，使用默认 [ContextMenu] 构造函数
   */
  fun createMenu(): ContextMenu?

  // * @param block 如果要自定义菜单项的结构比较复杂，造成默认的点击事件不生效，无法执行预定义的方法体，可自行调用该参数来执行；
  // *              该参数执行结果与预定义方法一致，普通应用请忽略此参数（java调用 block.invoke()）；
  // *              另外，如果selectable为true，并需要自定义菜单项，请自行调用block
  /**
   * 创建菜单项，可自定义菜单项类型及样式等
   *
   * @param name 注解的菜单项唯一名称
   * @param text 注解的菜单项文本
   * @param selectable 是否为checkbox菜单项
   * @param provision 默认的菜单项，可直接返回，也可新创建一个菜单项
   */
  fun createMenuItem(name: String, text: String, selectable: Boolean, provision: MenuItem): MenuItem
}
