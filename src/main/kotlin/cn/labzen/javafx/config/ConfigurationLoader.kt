package cn.labzen.javafx.config

import cn.labzen.javafx.exception.ApplicationBootException
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.security.AnyTypePermission
import java.net.URL

internal object ConfigurationLoader {

  fun load(resource: URL): App {
    val xs = XStream(AnnotationJavaReflectionProvider()).apply {
      XStream.setupDefaultSecurity(this)
      processAnnotations(arrayOf(App::class.java))
      addPermission(AnyTypePermission.ANY)
    }

    try {
      return xs.fromXML(resource) as App
    } catch (e: Exception) {
      throw ApplicationBootException(e, "无法反序列化配置文件内容。 from: $resource")
    }
  }
}
