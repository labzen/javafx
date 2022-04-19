package cn.labzen.javafx

import cn.labzen.javafx.initialize.LabzenApplicationInitializer
import cn.labzen.javafx.initialize.LabzenInitializerExecutor
import cn.labzen.javafx.preload.PreloadDetails
import cn.labzen.javafx.stage.LabzenStage
import cn.labzen.javafx.stage.StageHandler
import cn.labzen.javafx.view.LabzenView
import cn.labzen.logger.kotlin.logger
import javafx.application.Application
import javafx.application.Preloader
import javafx.stage.Stage
import java.util.concurrent.CountDownLatch

abstract class LabzenApplication : Application(), LabzenStage {

  private val logger = logger { }
  private var initializers: List<LabzenApplicationInitializer>? = null
  private lateinit var stageRef: Stage

  final override fun init() {
    prepareInitializers()
    executeInitializers()

    LabzenPlatform.container().loadIconsIfNecessary()
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

    StageHandler.go(viewMark = container.primaryView)
    stageRef.show()
  }

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
    val initializerCount = initializers.size

    val preloadDetails = PreloadDetails(initializerCount, weightAmounts)
    LabzenPlatform.container().preloadDetails.set(preloadDetails)

    this.initializers = initializers.toList()
  }

  private fun executeInitializers() {
    val preloadDetails = LabzenPlatform.container().preloadDetails.get()
    val countDown = CountDownLatch(preloadDetails.count)

    val executors = this.initializers!!.map { initializer ->
      LabzenInitializerExecutor(initializer, preloadDetails.checkIn(initializer))
    }
    executors.forEach {
      it.execute() { countDown.countDown() }
    }

    countDown.await()
    this.initializers = null
  }

  final override fun getStage(): Stage = stageRef

}
