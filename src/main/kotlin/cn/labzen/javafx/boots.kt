package cn.labzen.javafx

import cn.labzen.javafx.component.shortcuts.ShortcutsHandler
import cn.labzen.javafx.component.tray.TrayHandler
import cn.labzen.javafx.config.App
import cn.labzen.javafx.css.CssHandler
import cn.labzen.javafx.theme.ThemeHandler
import javafx.application.Platform

internal interface PreBootProcess {

  fun process(configuration: App)
}

internal class BasicProcess : PreBootProcess {

  override fun process(configuration: App) {
    if (configuration.meta.closeToTray) {
      // 设置为false后，所有窗体被关闭后，程序不会退出，除非主动调用Application.stop()方法
      Platform.setImplicitExit(false)
    }
  }
}

internal class TrayProcess : PreBootProcess {

  override fun process(configuration: App) {
    configuration.meta.tray?.run {
      TrayHandler.initialize(handler, icon, css)
      TrayHandler.create(tooltip ?: configuration.meta.title)
    }
  }
}

internal class ThemeProcess : PreBootProcess {

  override fun process(configuration: App) {
    configuration.meta.theme?.run {
      ThemeHandler.updateDefaultTheme(this)
    }
  }
}

internal class CssProcess : PreBootProcess {

  override fun process(configuration: App) {
    configuration.debug.css?.run {
      CssHandler.initialize(this.classpath)
    }
  }
}

internal class ShortcutsProcess : PreBootProcess {

  override fun process(configuration: App) {
    configuration.meta.shortcuts?.run {
      ShortcutsHandler.initialize(this.handler)
    }
  }
}
