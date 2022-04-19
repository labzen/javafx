package cn.labzen.javafx.component.shortcuts

import cn.labzen.cells.core.utils.Randoms
import cn.labzen.javafx.LabzenPlatform
import cn.labzen.javafx.exception.ApplicationBootException
import cn.labzen.logger.kotlin.logger
import com.github.kwhat.jnativehook.GlobalScreen
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import java.util.logging.Level
import java.util.logging.Logger

object ShortcutsHandler {

  private val logger = logger { }

  private val globalShortcuts = mutableMapOf<KeyCombine, String>()
  private val dynamicBlockFunctions = mutableMapOf<String, () -> Unit>()
  private lateinit var cls: Class<ShortcutsController>
  private lateinit var handler: ShortcutsController

  fun initialize(handlerClassName: String) {
    parseHandler(handlerClassName)

    GlobalScreen.registerNativeHook()

    Logger.getLogger(GlobalScreen::class.java.getPackage().name).also {
      it.level = Level.OFF
      it.useParentHandlers = false
    }

    GlobalScreen.addNativeKeyListener(GlobalKeyListenerProxy())
  }

  private fun parseHandler(handlerClassName: String) {
    // 托盘菜单处理类
    val c = try {
      Class.forName(handlerClassName)
    } catch (e: Exception) {
      throw ApplicationBootException(e, "找不到快捷键处理类：$handlerClassName")
    }
    cls = if (ShortcutsController::class.java.isAssignableFrom(c)) {
      @Suppress("UNCHECKED_CAST")
      c as Class<ShortcutsController>
    } else throw ApplicationBootException("快捷键处理类必须实现ShortcutsController接口：$handlerClassName")
    val sac = LabzenPlatform.container().springApplicationContext.get()
    handler = try {
      sac.getBean(cls)
    } catch (e: NoSuchBeanDefinitionException) {
      cls.getConstructor().newInstance()
    } catch (e: Exception) {
      throw ApplicationBootException(e, "无法获取（或创建）快捷键处理类实例：$cls")
    }

    val registeredKeys = handler.registerKey()

    for (rk in registeredKeys) {
      val method = try {
        cls.getMethod(rk.value)
      } catch (e: Exception) {
        logger.warn("快捷键处理类中的方法需要不带任何参数")
        null
      }

      registerKey(rk.key) {
        method?.invoke(handler)
      }
    }
  }

  /**
   * 注册一个全局快捷键
   */
  fun registerKey(kc: KeyCombine, block: () -> Unit) {
    val dbId = Randoms.string(16)
    dynamicBlockFunctions[dbId] = block
    globalShortcuts[kc] = dbId
  }

  fun matching(target: Set<Int>) {
    val found = globalShortcuts.keys.find {
      it.match(target)
    } ?: return

    val matchedCommand = globalShortcuts[found]?.let {
      dynamicBlockFunctions[it]
    }
    matchedCommand?.also { it() }
  }
}
