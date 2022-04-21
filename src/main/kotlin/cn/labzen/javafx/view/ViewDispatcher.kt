package cn.labzen.javafx.view

interface ViewDispatcher {

  /**
   * 决定要显示的视图名(fxml文件，忽略 .fxml 扩展名)，相对 “app/meta/structure/view”的指定目录下
   */
  fun dispatch(): String
}
