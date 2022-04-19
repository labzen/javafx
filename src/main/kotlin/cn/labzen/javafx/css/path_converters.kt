package cn.labzen.javafx.css

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * todo 这些converter在css这个功能模块中没啥用
 * 将类路径中的资源文件关联到磁盘上的物理文件
 */
interface CssPathConverter {

  fun convert(url: String): Path?
}

object CssFromMavenConverter : CssPathConverter {

  private val TARGET_CLASS_PATH = arrayOf("target/classes", "src/main/java", "src/main/resources")
  private val TARGET_TEST_CLASS_PATH = arrayOf("target/test-classes", "src/test/java", "src/test/resources")

  override fun convert(url: String): Path? {
    val paths = url.let {
      if (it.contains(TARGET_CLASS_PATH[0])) {
        TARGET_CLASS_PATH
      } else if (it.contains(TARGET_TEST_CLASS_PATH[0])) {
        TARGET_TEST_CLASS_PATH
      } else null
    } ?: return null

    for (i in 1..2) {
      val location = url.replace(paths[0], paths[i]).let {
        Paths.get(it)
      }
      if (Files.exists(location)) {
        return location
      }
    }
    return null
  }

}

object CssFromJarConverter : CssPathConverter {

  private val JAR_PATTERNS = arrayOf(
    Regex("jar:file:/(.*)/target/(.*)\\.jar!/(.*\\.css)"), // resource from maven jar in target directory
    Regex("jar:file:/(.*)/build/(.*)\\.jar!/(.*\\.css)") // resource from gradle jar in target directory
  )
  private val JAR_SOURCES_REPLACEMENTS = arrayOf(
    "src/main/java", "src/main/resources", "src/test/java", "src/test/resources"
  )

  override fun convert(url: String): Path? {
    val regex = JAR_PATTERNS.find {
      it.matches(url)
    } ?: return null

    val groups = regex.findAll(url).toList()
    return JAR_SOURCES_REPLACEMENTS.map {
      Paths.get("file:/${groups[0]}/${it}/${groups[2]}")
    }.find {
      Files.exists(it)
    }
  }
}
