package cn.labzen.javafx.exception

import cn.labzen.cells.core.exception.LabzenRuntimeException

/**
 * JavaFX通用异常
 */
class ApplicationException : LabzenRuntimeException {
  constructor(message: String, vararg arguments: Any) : super(message, *arguments)
  constructor(cause: Throwable) : super(cause)
  constructor(cause: Throwable, message: String, vararg arguments: Any) : super(cause, message, *arguments)
}

/**
 * JavaFX启动异常
 */
class ApplicationBootException : LabzenRuntimeException {
  constructor(message: String, vararg arguments: Any) : super(message, *arguments)
  constructor(cause: Throwable) : super(cause)
  constructor(cause: Throwable, message: String, vararg arguments: Any) : super(cause, message, *arguments)
}

/**
 * 窗体或视图操作异常
 */
class StageViewOperationException : LabzenRuntimeException {
  constructor(message: String, vararg arguments: Any) : super(message, *arguments)
  constructor(cause: Throwable) : super(cause)
  constructor(cause: Throwable, message: String, vararg arguments: Any) : super(cause, message, *arguments)
}

/**
 * 弹框或弹窗处理异常
 */
class DialogException : LabzenRuntimeException {
  constructor(message: String, vararg arguments: Any) : super(message, *arguments)
  constructor(cause: Throwable) : super(cause)
  constructor(cause: Throwable, message: String, vararg arguments: Any) : super(cause, message, *arguments)
}
