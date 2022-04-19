package cn.labzen.javafx.config

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

@XStreamAlias("app")
internal class App {
  lateinit var meta: Meta
  lateinit var debug: Debug
}

@XStreamAlias("meta")
internal class Meta {
  @XStreamAsAttribute
  lateinit var id: String

  @XStreamAsAttribute
  lateinit var title: String

  @XStreamAsAttribute
  var theme: String? = null

  @XStreamAsAttribute
  @XStreamAlias("close-to-tray-icon")
  var closeToTray: Boolean = false
  var structure = Structure()
  lateinit var bootstrap: Bootstrap
  var tray: Tray? = null
  var shortcuts: Shortcuts? = null
}

@XStreamAlias("structure")
internal class Structure {
  @XStreamAsAttribute
  var view = "view"

  @XStreamAsAttribute
  var style = "style"

  @XStreamAsAttribute
  var script = "script"

  @XStreamAsAttribute
  var font = "font"

  @XStreamAsAttribute
  var image = "image"

  @XStreamAsAttribute
  var icon = "icon"

  @XStreamAsAttribute
  var sound = "sound"

  @XStreamAsAttribute
  var video = "video"

  @XStreamAsAttribute
  var db = "db"
}

@XStreamAlias("bootstrap")
internal class Bootstrap {

  @XStreamAlias("application-class")
  @XStreamAsAttribute
  lateinit var application: String

  @XStreamAsAttribute
  lateinit var view: String

  @XStreamAsAttribute
  var icons: String? = null

  @XStreamAsAttribute
  @XStreamAlias("need-preload")
  var needPreload: String = "true"
  var preload: Preload? = null
}

@XStreamAlias("preload")
internal class Preload {
  @XStreamAsAttribute
  lateinit var view: String
}

@XStreamAlias("tray")
internal class Tray {

  @XStreamAsAttribute
  var icon: String? = null

  @XStreamAlias("handler-class")
  @XStreamAsAttribute
  lateinit var handler: String

  @XStreamAsAttribute
  var tooltip: String? = null

  @XStreamAsAttribute
  var css: String? = null
}

@XStreamAlias("shortcuts")
internal class Shortcuts {

  @XStreamAlias("handler-class")
  @XStreamAsAttribute
  lateinit var handler: String
}

@XStreamAlias("debug")
internal class Debug {

  var css: Css? = null
}

@XStreamAlias("css")
internal class Css {

  @XStreamAsAttribute
  var classpath: String? = null
}

