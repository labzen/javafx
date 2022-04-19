package cn.labzen.javafx.preload

import cn.labzen.javafx.LabzenPlatform
import cn.labzen.javafx.dialog.DialogHandler
import cn.labzen.javafx.initialize.InitializeFailedNotification
import cn.labzen.javafx.initialize.InitializeFinishedNotification
import cn.labzen.javafx.initialize.InitializeInformationNotification
import cn.labzen.javafx.initialize.InitializeStartNotification
import cn.labzen.javafx.stage.LabzenStage
import cn.labzen.javafx.view.ViewHandler
import cn.labzen.javafx.view.ViewWrapper
import cn.labzen.logger.kotlin.logger
import javafx.application.Preloader
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.WindowEvent

class LabzenPreloader : Preloader(), LabzenStage {

  private val logger = logger { }
  private lateinit var stageRef: Stage
  private var progressBar: ProgressBar? = null
  private var informationLabel: Label? = null

  override fun init() {
    LabzenPlatform.container().loadIconsIfNecessary()
  }

  override fun start(primaryStage: Stage) {
    stageRef = primaryStage

    stageRef.isResizable = false
    stageRef.initStyle(StageStyle.TRANSPARENT)
    stageRef.icons.addAll(LabzenPlatform.container().icons)

    val wrapper = createView()
    wrapper.attachTo(this)
    wrapper.createScene()
    stageRef.scene = wrapper.scene
    stageRef.scene.fill = null
    stageRef.show()
  }

  override fun getStage(): Stage =
    stageRef

  override fun customize(primaryStage: Stage) {
  }

  override fun closed(event: WindowEvent, primaryStage: Stage) {
  }

  override fun theme(): String? = null

  override fun handleApplicationNotification(pn: PreloaderNotification) {
    when (pn) {
      is InitializeStartNotification -> {
        informationLabel?.text = pn.notification()
      }
      is InitializeFinishedNotification -> {
        informationLabel?.text = pn.notification()
        progressBar?.progress = pn.progress
      }
      is InitializeInformationNotification -> {
        informationLabel?.text = pn.notification()
        progressBar?.progress = pn.progress
      }
      is InitializeFailedNotification -> {
        informationLabel?.text = pn.notification()
        if (pn.exit) {
          DialogHandler.createWindowBuilder()
            .error(
              throwable = pn.throwable,
              header = "Oops!! A serious exception occurred, and the app will be closed."
            )
            .shutdownWhenDialogClosed().show();
        } else {
          progressBar?.progress = pn.progress
          logger.error(pn.throwable)
        }
      }
    }
  }

  override fun handleStateChangeNotification(info: StateChangeNotification) {
    if (info.type == StateChangeNotification.Type.BEFORE_START) {
      logger.info("Primary stage view will be shown")
      stageRef.hide()
    }
  }

  private fun createView(): ViewWrapper =
    ViewHandler.loadView(LabzenPlatform.container().preloadView!!).also {
      it.controller?.run { findPreloadInformationControls(this) }
    }

  private fun findPreloadInformationControls(controller: Any) {
    if (PreloadView::class.java.isAssignableFrom(controller::class.java)) {
      val pc = controller as PreloadView
      progressBar = pc.progressBar()
      informationLabel = pc.progressDigestLabel()
    }
  }

}
