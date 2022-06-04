package cn.labzen.javafx.initialize

import cn.labzen.javafx.preload.PreloadDetails
import javafx.concurrent.Task
import kotlin.concurrent.thread

class LabzenInitializerExecutor(
  private val initializer: LabzenApplicationInitializer,
  private val record: PreloadDetails.Record
) : Task<Unit>() {

  init {
    initializer.notifyNotification = { message, step ->
      record.info(message, step)
    }
  }

  override fun call() {
    if (!initializer.available()) {
      record.finished(null, 0)
      return
    }

    record.start(initializer.startMessage())

    val startAt = System.currentTimeMillis()
    try {
      initializer.run()

      record.finished(initializer.finishedMessage(), (System.currentTimeMillis() - startAt).toInt())
    } catch (throwable: Throwable) {
      record.failed(throwable, (System.currentTimeMillis() - startAt).toInt(), initializer.exitIfException(throwable))
    }
  }

  fun execute(callback: () -> Unit) {
    thread(isDaemon = true, name = "PreInit-${initializer::javaClass.name}") {
      call()
      callback()
    }
  }
}
