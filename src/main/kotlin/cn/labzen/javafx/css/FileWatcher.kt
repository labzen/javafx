package cn.labzen.javafx.css

import cn.labzen.logger.kotlin.logger
import com.sun.nio.file.SensitivityWatchEventModifier
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.*
import java.nio.file.WatchEvent
import kotlin.concurrent.thread

internal object FileWatcher {

  private val logger = logger { }

  private val watchService = FileSystems.getDefault().newWatchService()
  private var interrupted = true

  private val kinds = arrayOf<WatchEvent.Kind<*>>(ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE)
  private val watchedDirectories = mutableSetOf<Path>()

  private val watching = {
    while (!interrupted) {
      val watchKey = watchService!!.take()

      val directory = (watchKey.watchable() as Path).toAbsolutePath().normalize()
      val pollEvents = watchKey.pollEvents()

      for (pe in pollEvents) {
        val kind = pe.kind()
        if (!kinds.contains(kind)) {
          continue
        }

        val event = pe as WatchEvent<*>
        val context = event.context() as Path
        val modifiedFile = directory.resolve(context).toAbsolutePath().normalize()

        if (kind == ENTRY_MODIFY) {
          CssHandler.updateStylesheets(modifiedFile)
        }
      }

      if (!watchKey.reset()) {
        interrupted = true
        // todo 通知异常终止
        logger.error("样式表文件监控线程异常，监控终止")
        break
      }
    }
  }

  fun watch(source: Path) {
    val directory = source.parent
    if (watchedDirectories.contains(directory)) {
      return
    }

    watchedDirectories.add(directory)
    directory.register(watchService, kinds, SensitivityWatchEventModifier.HIGH)
  }

  // 开启文件监视线程，监视对象后期陆续设置
  fun start() {
    if (interrupted) {
      interrupted = false
      thread(name = "Labzen-CSS-File-Watcher", block = watching)
      logger.info("DEBUG: 动态样式表变更监控功能已开启（请在生产环境中移除掉所有的@MonitorCss注解）")
    }
  }

  fun stop() {
    interrupted = true
  }
}
