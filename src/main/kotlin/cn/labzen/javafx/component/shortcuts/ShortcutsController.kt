package cn.labzen.javafx.component.shortcuts

interface ShortcutsController {

  /**
   * 注册全局快捷键
   * @return key: 快捷键信息；value: 快捷键对应的方法名
   */
  fun registerKey(): Map<KeyCombine, String>
}
