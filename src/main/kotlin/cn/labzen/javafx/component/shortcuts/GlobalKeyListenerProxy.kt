package cn.labzen.javafx.component.shortcuts

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener

class GlobalKeyListenerProxy : NativeKeyListener {

  private val cache = sortedSetOf<Int>()

  override fun nativeKeyPressed(nativeEvent: NativeKeyEvent) {
    val sig = KeyInfo.fastSignature(nativeEvent.keyCode, nativeEvent.keyLocation)
    cache.add(sig)

    if (!nativeEvent.isActionKey) {
      ShortcutsHandler.matching(cache)
    }
  }

  override fun nativeKeyReleased(nativeEvent: NativeKeyEvent) {
    val sig = KeyInfo.fastSignature(nativeEvent.keyCode, nativeEvent.keyLocation)
    cache.remove(sig)
  }

}
