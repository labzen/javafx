package cn.labzen.javafx

import cn.labzen.javafx.exception.ApplicationBootException
import cn.labzen.javafx.initialize.LabzenApplicationInitializer
import cn.labzen.javafx.initialize.LabzenInitializerExecutor
import cn.labzen.javafx.preload.PreloadDetails
import cn.labzen.javafx.stage.LabzenStage
import cn.labzen.javafx.stage.StageHandler
import cn.labzen.javafx.view.LabzenView
import javafx.application.Application
import javafx.application.Preloader
import javafx.stage.Stage
import java.util.concurrent.CountDownLatch

abstract class LabzenApplication : Application(), LabzenStage {

  private var allInitializers: List<List<LabzenApplicationInitializer>>? = null
  private lateinit var stageRef: Stage

  final override fun init() {
    LabzenPlatform.container().loadIconsIfNecessary()

    prepareInitializers()
    executeInitializers()

    notifyPreloader(Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_START))
  }

  final override fun start(primaryStage: Stage) {
    stageRef = primaryStage
    StageHandler.setPrimaryStage(this)

    val container = LabzenPlatform.container()

    stageRef.icons.addAll(container.icons)
    stageRef.title = container.appTitle

    customize(stageRef)
    stageRef.setOnCloseRequest {
      closed(it, stageRef)
    }

    StageHandler.go(viewMark = primaryView())
    stageRef.show()
  }

  private fun primaryView(): String =
    LabzenPlatform.container().primaryDispatcher?.let {
      try {
        val dispatcher = it.getDeclaredConstructor().newInstance()
        dispatcher.dispatch()
      } catch (e: Exception) {
        throw ApplicationBootException(e, "无法实例化或正确执行视图调度：$it")
      }
    } ?: LabzenPlatform.container().primaryView!!

  /**
   * Application主界面跟随默认皮肤，这里不需要设置，如确需改变，请通过[LabzenView]设定
   */
  final override fun theme(): String? = null

  /**
   * 整个应用被退出（主Stage）时调用，可处理停闭服务、释放资源等操作，请限制在当前Stage涉及的范围，
   */
  abstract fun destroy()

  final override fun stop() {
    destroy()
    LabzenJavaFX.terminate()
  }

  private fun prepareInitializers() {
    val springApplicationContext = LabzenPlatform.container().springApplicationContext.get()

    val initializers = springApplicationContext.getBeansOfType(LabzenApplicationInitializer::class.java).values
    val weightAmounts = initializers.sumOf {
      it.weight()
    }.toDouble()

    val preloadDetails = PreloadDetails(weightAmounts)
    LabzenPlatform.container().preloadDetails.set(preloadDetails)

    this.allInitializers = initializers.groupBy { it.order() }.toSortedMap().values.toList()
  }

  private fun executeInitializers() {
    // val preloadDetails = LabzenPlatform.container().preloadDetails.get()

    for (ois in allInitializers!!) {
      executeOrderedInitializer(ois)
    }

    this.allInitializers = null
    Thread.sleep(1000)
  }

  private fun executeOrderedInitializer(orderedInitializers: List<LabzenApplicationInitializer>) {
    val countDown = CountDownLatch(orderedInitializers.size)
    val preloadDetails = LabzenPlatform.container().preloadDetails.get()

    val executors = orderedInitializers.map {
      LabzenInitializerExecutor(it, preloadDetails.checkIn(it))
    }

    executors.forEach {
      it.execute() { countDown.countDown() }
    }

    countDown.await()
  }

  final override fun getStage(): Stage = stageRef

}
