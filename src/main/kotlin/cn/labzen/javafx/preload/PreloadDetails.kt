package cn.labzen.javafx.preload

import cn.labzen.cells.core.kotlin.throwRuntimeIf
import cn.labzen.javafx.exception.ApplicationBootException
import cn.labzen.javafx.initialize.*
import com.google.common.util.concurrent.AtomicDouble
import com.sun.javafx.application.LauncherImpl.notifyPreloader
import java.time.LocalDateTime

class PreloadDetails(
  val count: Int,
  val weightAmounts: Double
) {
  private val records = mutableMapOf<Class<out LabzenApplicationInitializer>, Record>()

  val progress = AtomicDouble(0.0)

  fun checkIn(initializer: LabzenApplicationInitializer): Record =
    Record(initializer::class.java, initializer.weight().toDouble()).also {
      records[initializer::class.java] = it
    }

  inner class Record(
    private val cls: Class<out LabzenApplicationInitializer>,
    weight: Double
  ) {
    private val progressProportion = weight / weightAmounts
    private val step = AtomicDouble(0.0)
    private val messages = mutableListOf<Message>()

    fun start(message: String?) {
      val msg = message ?: ""
      messages.add(Message(now(), msg, RecordMessageStatus.START))
      notifyPreloader(null, InitializeStartNotification(cls, msg))
    }

    fun info(message: String, step: Double) {
      // 进度的步进
      val currentStep = this.step.addAndGet(step)
      (currentStep > 1.0).throwRuntimeIf {
        ApplicationBootException("检测到进度条进度微调幅度大于1.0")
      }

      val currentProgress = progress.addAndGet(step * progressProportion)

      messages.add(Message(now(), message, RecordMessageStatus.INFO))
      notifyPreloader(
        null,
        InitializeInformationNotification(cls, message, currentProgress)
      )
    }

    fun finished(message: String?, elapsed: Int) {
      val remainderStep = 1 - this.step.get()
      val currentProgress = progress.addAndGet(remainderStep * progressProportion)

      val msg = message ?: ""
      messages.add(Message(now(), msg, RecordMessageStatus.FINISHED))
      notifyPreloader(null, InitializeFinishedNotification(cls, currentProgress, elapsed, msg))
    }

    fun failed(throwable: Throwable, elapsed: Int, exit: Boolean) {
      val remainderStep = 1 - this.step.get()
      val currentProgress = progress.addAndGet(remainderStep * progressProportion)

      messages.add(
        Message(
          now(),
          throwable.message ?: "unknown throwable information",
          if (exit) RecordMessageStatus.FATAL else RecordMessageStatus.FAILED
        )
      )

      notifyPreloader(
        null,
        InitializeFailedNotification(cls, throwable, currentProgress, elapsed, exit)
      )
    }

    private fun now() =
      LocalDateTime.now()
  }

  inner class Message(
    val time: LocalDateTime,
    val message: String,
    val status: RecordMessageStatus
  )

  enum class RecordMessageStatus {
    INFO, START, FINISHED, FAILED, FATAL
  }

}
