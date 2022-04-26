package cn.labzen.javafx.css

import cn.labzen.cells.core.definition.Constants.Companion.PACKAGE_ROOT
import cn.labzen.javafx.stage.LabzenStage
import cn.labzen.javafx.stage.LabzenStageContainer
import cn.labzen.javafx.stage.StageHandler
import cn.labzen.javafx.view.LabzenView
import cn.labzen.javafx.view.ViewHandler
import cn.labzen.javafx.view.ViewWrapper
import cn.labzen.logger.kotlin.logger
import javafx.application.Platform
import javafx.collections.MapChangeListener
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.core.type.filter.AssignableTypeFilter
import java.nio.file.Path

object CssHandler {

  private val logger = logger { }

  private val STAGE_CLASS = LabzenStage::class.java
  private val VIEW_CLASS = LabzenView::class.java

  private val stageClasses = mutableSetOf<Class<in LabzenStage>>()
  private val viewClasses = mutableSetOf<Class<in LabzenView>>()

  private val monitoredStylesheetsCache = mutableMapOf<Path, MonitoredStylesheets>()

  init {
    // 监听 Stage 的变更，做相应的进一步样式表监听处理
    StageHandler.allStages().addListener(MapChangeListener {
      if (it.wasAdded()) {
        registerStageInstance(it.valueAdded)
      } else {
        unregisterStageInstance(it.valueRemoved)
      }
    })
    // 监听 View 的变更，做相应的进一步样式表监听处理
    ViewHandler.allViews().addListener(MapChangeListener {
      if (it.wasAdded()) {
        registerViewInstance(it.valueAdded)
      } else {
        unregisterViewInstance(it.valueRemoved)
      }
    })
  }

  internal fun initialize(classpath: String?) {
    // 扫描代码中注解了 MonitorCss 的 Stage 或 View，作为可监视样式表动态变更的对象
    val provider = ClassPathScanningCandidateComponentProvider(false)
    provider.resourceLoader = PathMatchingResourcePatternResolver(CssHandler::class.java.classLoader)
    val annotationTypeFilter = AnnotationTypeFilter(MonitorCss::class.java)
    val assignableStageTypeFilter = AssignableTypeFilter(LabzenStage::class.java)
    val assignableViewTypeFilter = AssignableTypeFilter(LabzenView::class.java)
    provider.addIncludeFilter { mr, mrf ->
      annotationTypeFilter.match(mr, mrf) &&
          (assignableStageTypeFilter.match(mr, mrf) || assignableViewTypeFilter.match(mr, mrf))
    }

    val foundComponents = provider.findCandidateComponents(classpath ?: PACKAGE_ROOT)
    foundComponents.forEach {
      val beanClassName = it.beanClassName
      if (it.isAbstract) {
        logger.warn("无法监视抽象类元素的样式表更新: $beanClassName")
      } else {
        val clazz = Class.forName(beanClassName)
        registerClass(clazz)
      }
    }

    // 启动文件监视
    FileWatcher.start()
  }

  /**
   * 暂停样式表的动态变更监控
   */
  @JvmStatic
  fun suspend() {
    FileWatcher.stop()
  }

  /**
   * 恢复样式表的动态变更监控
   */
  @JvmStatic
  fun resume() {
    FileWatcher.start()
  }

  /**
   * 项目启动时，先扫描可监视样式表动态变更的元素类，并记住对应的类
   */
  @Suppress("UNCHECKED_CAST")
  private fun registerClass(clazz: Class<*>) {
    when {
      STAGE_CLASS.isAssignableFrom(clazz) -> stageClasses.add(clazz as Class<LabzenStage>)
      VIEW_CLASS.isAssignableFrom(clazz) -> viewClasses.add(clazz as Class<LabzenView>)
      else -> logger.error("还没到支持第三个大类别的时候呢")
    }
  }

  internal fun registerMonitoredStylesheets(ms: MonitoredStylesheets) {
    monitoredStylesheetsCache.putIfAbsent(ms.path!!, ms)
  }

  internal fun updateStylesheets(modifiedFile: Path) {
    Platform.runLater {
      monitoredStylesheetsCache[modifiedFile]?.run {
        applyFile(modifiedFile)
      } ?: logger.warn("居然没有监听样式文件：$modifiedFile ！")
    }
  }

  // ===============================================================================================

  private fun registerStageInstance(instance: LabzenStageContainer) {
    if (!stageClasses.contains(instance.javaClass)) {
      return
    }
    MonitoredTargetRegistry.registerStage(instance)
  }

  private fun unregisterStageInstance(instance: LabzenStageContainer) {
  }

  private fun registerViewInstance(instance: ViewWrapper) {
    instance.controller ?: return

    val controller = instance.controller
    if (!viewClasses.contains(controller.javaClass)) {
      return
    }
    MonitoredTargetRegistry.registerView(controller)
  }

  private fun unregisterViewInstance(instance: ViewWrapper) {

  }
}
