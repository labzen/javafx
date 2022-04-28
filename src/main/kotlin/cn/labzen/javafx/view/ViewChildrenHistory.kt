package cn.labzen.javafx.view

import cn.labzen.cells.core.utils.Strings
import java.util.*

internal class ViewChildrenHistory {

  private val stack = ArrayDeque<ViewWrapper>()

  /**
   * 将视图压入历史堆栈
   */
  fun push(wrapper: ViewWrapper) {
    stack.push(wrapper)
  }

  /**
   * 弹出历史栈顶的视图
   */
  fun pop(): ViewWrapper? =
    if (stack.isEmpty())
      null
    else
      stack.pop()

  /**
   * 弹出历史栈顶的视图，并返回当前栈顶视图
   *
   * @return Pair中第一个参数为弹出的原栈顶视图，第二个参数为弹出后当前栈顶视图
   */
  fun popTopAndPeekNext(): Pair<ViewWrapper?, ViewWrapper?> =
    if (stack.isEmpty()) {
      Pair(null, null)
    } else {
      Pair(stack.pop(), stack.peek())
    }

  /**
   * 从历史栈顶开始弹出所有视图，直到遇见参数指定的视图，将其作为新的栈顶。如果参数中的视图不存在于栈内，则栈将被清空
   *
   * @return 已弹出的所有视图列表，列表的0索引为原栈顶，可能为空列表
   */
  fun popUntil(target: ViewWrapper): List<ViewWrapper> {
    val popped = mutableListOf<ViewWrapper>()
    while (stack.isNotEmpty()) {
      if (stack.peek().id == target.id) {
        break
      }

      popped.add(stack.pop())
    }
    return popped
  }

  fun peek(): ViewWrapper? {
    return stack.peek()
  }

  /**
   * 在历史栈中查找相应的视图
   */
  fun search(viewId: String?, viewName: String?): ViewWrapper? {
    if (Strings.isAllBlank(viewId, viewName)) return null
    if (stack.isEmpty()) return null

    return stack.find {
      it.id == viewId || it.name == viewName
    }
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
