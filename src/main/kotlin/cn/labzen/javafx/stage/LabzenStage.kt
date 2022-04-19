package cn.labzen.javafx.stage

import javafx.stage.Stage
import javafx.stage.WindowEvent

/**
 * 标识一个独立窗口
 */
interface LabzenStage {

  fun getStage(): Stage

  // todo customize 和 closed 只在 Application 中应用，还未在普通窗体 Stage 中使用
  /**
   * 对 Stage 窗口进行定制化，在窗口显示前被调用 void start(Stage primaryStage)
   */
  fun customize(primaryStage: Stage)

  /**
   * 窗口退出时被调用，可处理停闭服务、释放资源等操作，请注意范围（尽量控制在窗口相关的资源、服务）
   */
  fun closed(event: WindowEvent, primaryStage: Stage)

  /**
   * 窗体应用的皮肤 ***目录***  路径，无皮肤路径指定，使用全局默认皮肤样式 - (通过 app.xml 设定)
   */
  fun theme(): String?
}
