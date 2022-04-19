package cn.labzen.javafx.config

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException

import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider
import java.lang.reflect.Field

class AnnotationJavaReflectionProvider : PureJavaReflectionProvider() {

  /**
   * 防止默认值被覆盖
   */
  override fun writeField(`object`: Any, fieldName: String, value: Any?, definedIn: Class<*>) {
    val field: Field = fieldDictionary.field(`object`.javaClass, fieldName, definedIn)
    validateFieldAccess(field)

    field.isAccessible = true
    val originalValue: Any? = field.get(`object`)

    val acceptable = value ?: originalValue

    try {
      field.set(`object`, acceptable)
    } catch (e: IllegalArgumentException) {
      throw ObjectAccessException("Could not set field " + `object`.javaClass + "." + field.name, e)
    } catch (e: IllegalAccessException) {
      throw ObjectAccessException("Could not set field " + `object`.javaClass + "." + field.name, e)
    }
  }

}
