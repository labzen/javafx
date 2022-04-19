package cn.labzen.javafx.dialog

import javafx.event.Event
import javafx.event.EventType

class DialogElementEvent(eventType: EventType<out Event>) : Event(eventType) {

  companion object {
    val OPENED_TYPE = EventType<DialogElementEvent>("LYNX_DIALOG_ELEMENT_OPENED")
    val OPENED_EVENT = DialogElementEvent(OPENED_TYPE)
    val CLOSED_TYPE = EventType<DialogElementEvent>("LYNX_DIALOG_ELEMENT_CLOSED")
    val CLOSED_EVENT = DialogElementEvent(CLOSED_TYPE)
  }
}

class DialogWindowEvent(eventType: EventType<out Event>) : Event(eventType) {

  companion object {
    val OPENED_TYPE = EventType<DialogWindowEvent>("LYNX_DIALOG_WINDOW_OPENED")
    val OPENED_EVENT = DialogWindowEvent(OPENED_TYPE)
    val CLOSED_TYPE = EventType<DialogWindowEvent>("LYNX_DIALOG_WINDOW_CLOSED")
    val CLOSED_EVENT = DialogWindowEvent(CLOSED_TYPE)
  }
}
