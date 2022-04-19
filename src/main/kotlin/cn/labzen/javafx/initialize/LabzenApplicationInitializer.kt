package cn.labzen.javafx.initialize

/**
 * Java FX应用初始化器，用于在应用启动时，对单一内容的准备工作，请使用@Component注册
 */
abstract class LabzenApplicationInitializer {

  internal lateinit var notifyNotification: (message: String, step: Double) -> Unit

  /**
   * 权重，当初始化完成后，进度的计算。数值越大，代表初始化器的执行时间越长，或代表初始化器执行的内容约重要，可根据开发者对代码的认知设置，默认所有权重相等
   */
  open fun weight(): Int = 1

  /**
   * 初始化器开始执行前输出的信息
   */
  open fun startMessage(): String? = null

  /**
   * 初始化器执行结束后输出的信息
   */
  open fun finishedMessage(): String? = null

  /**
   * 当初始化发生异常时，是否退出程序，可根据不同的异常信息做不同处理，默认false
   */
  open fun exitIfException(throwable: Throwable): Boolean = false

  /**
   * 初始执行
   */
  abstract fun run()

  /**
   * 执行期间输出信息，如果同时需要调整进度条，可传入 [0.0,1.0) 的数值，进度条会在当前执行器的进度权重范围内进行微调，一般用于较长时间执行的初始化工作
   * @param step 信息步进，一般用于执行时间较长的初始化，执行中打印信息，带有细粒度的进度变更，可为0，但不能大于1等于；注意：同一个初始化器中，所有的step相加应小于等于1.0
   */
  @JvmOverloads
  protected fun info(message: String, step: Double? = null) {
    notifyNotification(message, step ?: 0.0)
  }
}
