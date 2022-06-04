package cn.labzen.javafx.stage

import cn.labzen.javafx.view.ViewHandler
import cn.labzen.javafx.view.ViewWrapper
import java.util.*

internal class StageViewHistory {

  private val stack = ArrayDeque<ViewWrapper>()

  fun loadAndPush(viewName: String, parameters: Map<String, Any>?): ViewWrapper =
    ViewHandler.loadView(viewName, parameters).also {
      // it.updateParameter(parameters)
      stack.push(it)
    }

  fun searchAndPop(viewMark: String?, parameters: Map<String, Any>?): ViewWrapper? {
    if (viewMark == null) {
      val peekSecond = stack.elementAt(1)
      return if (peekSecond != null) {
        stack.pop()
        stack.peek()
      } else null
    }

    val existed = stack.find { it.id == viewMark || it.name == viewMark } ?: return null

    while (stack.isNotEmpty()) {
      val elem = stack.peek()
      if (elem.id == existed.id) {
        elem.updateParameter(parameters)
        return elem
      }

      stack.pop()
    }
    // 不阔能
    return null
  }
}
