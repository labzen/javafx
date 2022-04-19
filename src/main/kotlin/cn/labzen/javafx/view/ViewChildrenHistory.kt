package cn.labzen.javafx.view

import java.util.*

internal class ViewChildrenHistory {

  private val stack = ArrayDeque<ViewWrapper>()

  fun push(wrapper: ViewWrapper) {
    stack.push(wrapper)
  }

  fun searchAndPop(viewId: String?, viewName: String?, parameters: Map<String, Any>?): ViewWrapper? {
    if (viewId == null && viewName == null) {
      val peekSecond = stack.elementAt(1)
      return if (peekSecond != null) {
        stack.pop()
        stack.peek()
      } else null
    }

    val existed = if (viewId != null) {
      stack.find { it.id == viewId }
    } else {
      stack.find { it.name == viewName }
    }
    existed ?: return null

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
