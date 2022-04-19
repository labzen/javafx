package cn.labzen.javafx.component.tray

import javafx.collections.ObservableList
import java.lang.annotation.Inherited

/**
 * 标记一个实例方法作为一个托盘菜单项，方法无返回值（如有返回值会被忽略掉）
 *
 * @param name 托盘菜单的唯一标识名，借由该参数对菜单形成展示结构；
 *             **1. 普通菜单项，使用纯字母（+数字）来标识；**
 *             **2. 拥有子菜单的母菜单项，标识名最后一个字符以'/'斜线结尾；**
 *             **3. 子菜单项的标识名符合第1条，但标识名前缀必须带有母菜单项的标识名；**
 *             **4. 符合第2条的母菜单项，需要用注解修饰一个空的方法，用来标识菜单项位置等信息，空方法将被忽略，并且selectable参数无效；**
 *             **5. 如果子菜单项的标识前缀定义找不到对应的母菜单项注解，将不会生效**
 * @param text 托盘菜单项的文字
 * @param group 托盘菜单项的分组，该参数相同的菜单项分为一组，从0开始编号，越小排在菜单越下方，可不连续；两个不同的组之间，有横线分隔
 * @param order 托盘菜单项在分组内的显示顺序，该参数影响的是在同一个分组内的顺序，从0开始编号，越小排在菜单越下方，可不连续，如遇到相同的，则随机排序
 * @param selectable 标识托盘菜单项是否可被选中（checkbox）
 */
@Inherited
@Target(AnnotationTarget.FUNCTION)
annotation class TrayItem(
  val name: String,
  val text: String,
  val group: Int = 0,
  val order: Int = 0,
  val selectable: Boolean = false
)

/**
 * 标识一个存储需要禁用掉的托盘菜单项 name （参考TrayItem注解）的可被监听的队列
 *
 * **被标识的属性类型必须为[ObservableList]&lt;String>**，元素为[TrayItem.name]
 */
@Inherited
@Target(AnnotationTarget.FIELD)
annotation class TrayMenuDisabledItems

/**
 * 标识一个存储需要隐藏掉的托盘菜单项 name （参考TrayItem注解）的可被监听的队列
 *
 * **被标识的属性类型必须为[ObservableList]&lt;String>**，元素为[TrayItem.name]
 */
@Inherited
@Target(AnnotationTarget.FIELD)
annotation class TrayMenuHiddenItems
