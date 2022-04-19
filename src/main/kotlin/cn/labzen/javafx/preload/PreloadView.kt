package cn.labzen.javafx.preload

import cn.labzen.javafx.view.LabzenView
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar

/**
 * 实现此接口，以自动化的方式来使用进度条
 */
abstract class PreloadView : LabzenView() {

  /**
   * 返回预加载视图中负责显示进度的进度条控件
   */
  abstract fun progressBar(): ProgressBar

  /**
   * 返回预加载视图中负责显示进度信息摘要的Label控件，一般用于生产部署后的应用界面，不指定则加载信息不显示，返回null则不显示信息摘要
   */
  abstract fun progressDigestLabel(): Label?
}
