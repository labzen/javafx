@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package cn.labzen.javafx.component.shortcuts

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.*

/**
 * 键信息
 *
 * @param code 指向[NativeKeyEvent.keyCode]
 * @param location 指向[NativeKeyEvent.keyLocation]，并在类内部相对应的分配一个[region]用于键的唯一签名
 */
data class KeyInfo(
  val code: Int,
  val location: Int,
  val actionKey: Boolean
) {

  private val region = region(location)

  fun signature() =
    region + code

  companion object {
    private fun region(l: Int) =
      when (l) {
        KEY_LOCATION_UNKNOWN -> 0
        KEY_LOCATION_STANDARD -> 1000
        KEY_LOCATION_LEFT -> 2000
        KEY_LOCATION_RIGHT -> 3000
        KEY_LOCATION_NUMPAD -> 4000
        else -> 9000
      }

    fun fastSignature(code: Int, location: Int) =
      region(location) + code
  }
}

class KeyCombine(vararg key: Int) {

  private val keys = key.sortedArray()

  fun match(target: Set<Int>): Boolean {
    if (keys.size != target.size) {
      return false
    }

    val targetArray = target.toIntArray()
    return keys contentEquals targetArray
  }

}

class Keys {

  companion object {
    val names: Map<KeyInfo, String> by lazy {
      mapOf(
        Pair(Esc, "Esc"),
        Pair(F1, "F1"),
        Pair(F2, "F2"),
        Pair(F3, "F3"),
        Pair(F4, "F4"),
        Pair(F5, "F5"),
        Pair(F6, "F6"),
        Pair(F7, "F7"),
        Pair(F8, "F8"),
        Pair(F9, "F9"),
        Pair(F10, "F10"),
        Pair(F11, "F11"),
        Pair(F12, "F12"),

        Pair(UNQUOTE, "反引号`"),
        Pair(NUM_1, "数字1"),
        Pair(NUM_2, "数字2"),
        Pair(NUM_3, "数字3"),
        Pair(NUM_4, "数字4"),
        Pair(NUM_5, "数字5"),
        Pair(NUM_6, "数字6"),
        Pair(NUM_7, "数字7"),
        Pair(NUM_8, "数字8"),
        Pair(NUM_9, "数字9"),
        Pair(NUM_0, "数字0"),
        Pair(MINUS, "减号-"),
        Pair(EQUALS, "等号="),
        Pair(BACKSPACE, "退格"),

        Pair(TAB, "TAB"),
        Pair(LETTER_Q, "Q"),
        Pair(LETTER_W, "W"),
        Pair(LETTER_E, "E"),
        Pair(LETTER_R, "R"),
        Pair(LETTER_T, "T"),
        Pair(LETTER_Y, "Y"),
        Pair(LETTER_U, "U"),
        Pair(LETTER_I, "I"),
        Pair(LETTER_O, "O"),
        Pair(LETTER_P, "P"),
        Pair(OPEN_BRACKET, "左方括号"),
        Pair(CLOSE_BRACKET, "右方括号"),
        Pair(BACK_SLASH, "反斜线\\"),

        Pair(CAPS_LOCK, "大小写转换键"),
        Pair(LETTER_A, "A"),
        Pair(LETTER_S, "S"),
        Pair(LETTER_D, "D"),
        Pair(LETTER_F, "F"),
        Pair(LETTER_G, "G"),
        Pair(LETTER_H, "H"),
        Pair(LETTER_J, "J"),
        Pair(LETTER_K, "K"),
        Pair(LETTER_L, "L"),
        Pair(SEMICOLON, "分号"),
        Pair(QUOTE, "引号"),
        Pair(ENTER, "回车"),

        Pair(LEFT_SHIFT, "左SHIFT"),
        Pair(LETTER_Z, "Z"),
        Pair(LETTER_X, "X"),
        Pair(LETTER_C, "C"),
        Pair(LETTER_V, "V"),
        Pair(LETTER_B, "B"),
        Pair(LETTER_N, "N"),
        Pair(LETTER_M, "M"),
        Pair(COMMA, "逗号"),
        Pair(PERIOD, "句号"),
        Pair(SLASH, "斜线/"),
        Pair(RIGHT_SHIFT, "右SHIFT"),

        Pair(LEFT_CTRL, "左CTRL"),
        Pair(RIGHT_CTRL, "右CTRL"),
        Pair(LEFT_ALT, "左ALT"),
        Pair(RIGHT_ALT, "右ALT"),
        Pair(LEFT_META, "左WIN键"),
        Pair(RIGHT_META, "右WIN键"),
        Pair(SPACE, "SPACE"),

        Pair(UP, "上键"),
        Pair(DOWN, "下键"),
        Pair(LEFT, "左键"),
        Pair(RIGHT, "右键"),

        Pair(PRINT_SCREEN, "PRINT SCREEN"),
        Pair(SCROLL_LOCK, "SCROLL LOCK"),
        Pair(PAUSE, "PAUSE"),
        Pair(INSERT, "INSERT"),
        Pair(DELETE, "DELETE"),
        Pair(HOME, "HOME"),
        Pair(END, "END"),
        Pair(PAGE_UP, "PAGE UP"),
        Pair(PAGE_DOWN, "PAGE DOWN"),

        Pair(NUM_LOCK, "小键盘数字转换键"),
        Pair(NUM_ADD, "加+"),
        Pair(NUM_SUBTRACT, "减-"),
        Pair(NUM_MULTIPLY, "乘*"),
        Pair(NUM_DIVIDE, "除/"),
        Pair(NUM_ENTER, "小键盘回车"),
        Pair(NUM_DELETE, "小键盘DELETE"),
        Pair(NUM_ISLET_1, "小键盘1"),
        Pair(NUM_ISLET_2, "小键盘2"),
        Pair(NUM_ISLET_3, "小键盘3"),
        Pair(NUM_ISLET_4, "小键盘4"),
        Pair(NUM_ISLET_5, "小键盘5"),
        Pair(NUM_ISLET_6, "小键盘6"),
        Pair(NUM_ISLET_7, "小键盘7"),
        Pair(NUM_ISLET_8, "小键盘8"),
        Pair(NUM_ISLET_9, "小键盘9"),
        Pair(NUM_ISLET_0, "小键盘0")
      )
    }

    // 标准键盘第一行
    val Esc = KeyInfo(1, KEY_LOCATION_STANDARD, false)
    val F1 = KeyInfo(59, KEY_LOCATION_STANDARD, true)
    val F2 = KeyInfo(60, KEY_LOCATION_STANDARD, true)
    val F3 = KeyInfo(61, KEY_LOCATION_STANDARD, true)
    val F4 = KeyInfo(62, KEY_LOCATION_STANDARD, true)
    val F5 = KeyInfo(63, KEY_LOCATION_STANDARD, true)
    val F6 = KeyInfo(64, KEY_LOCATION_STANDARD, true)
    val F7 = KeyInfo(65, KEY_LOCATION_STANDARD, true)
    val F8 = KeyInfo(66, KEY_LOCATION_STANDARD, true)
    val F9 = KeyInfo(67, KEY_LOCATION_STANDARD, true)
    val F10 = KeyInfo(68, KEY_LOCATION_STANDARD, true)
    val F11 = KeyInfo(87, KEY_LOCATION_STANDARD, true)
    val F12 = KeyInfo(88, KEY_LOCATION_STANDARD, true)

    // 标准键盘第二行
    val UNQUOTE = KeyInfo(41, KEY_LOCATION_STANDARD, false)
    val NUM_1 = KeyInfo(2, KEY_LOCATION_STANDARD, false)
    val NUM_2 = KeyInfo(3, KEY_LOCATION_STANDARD, false)
    val NUM_3 = KeyInfo(4, KEY_LOCATION_STANDARD, false)
    val NUM_4 = KeyInfo(5, KEY_LOCATION_STANDARD, false)
    val NUM_5 = KeyInfo(6, KEY_LOCATION_STANDARD, false)
    val NUM_6 = KeyInfo(7, KEY_LOCATION_STANDARD, false)
    val NUM_7 = KeyInfo(8, KEY_LOCATION_STANDARD, false)
    val NUM_8 = KeyInfo(9, KEY_LOCATION_STANDARD, false)
    val NUM_9 = KeyInfo(10, KEY_LOCATION_STANDARD, false)
    val NUM_0 = KeyInfo(11, KEY_LOCATION_STANDARD, false)
    val MINUS = KeyInfo(12, KEY_LOCATION_STANDARD, false)
    val EQUALS = KeyInfo(13, KEY_LOCATION_STANDARD, false)
    val BACKSPACE = KeyInfo(14, KEY_LOCATION_STANDARD, false)

    // 标准键盘第三行
    val TAB = KeyInfo(15, KEY_LOCATION_STANDARD, false)
    val LETTER_Q = KeyInfo(16, KEY_LOCATION_STANDARD, false)
    val LETTER_W = KeyInfo(17, KEY_LOCATION_STANDARD, false)
    val LETTER_E = KeyInfo(18, KEY_LOCATION_STANDARD, false)
    val LETTER_R = KeyInfo(19, KEY_LOCATION_STANDARD, false)
    val LETTER_T = KeyInfo(20, KEY_LOCATION_STANDARD, false)
    val LETTER_Y = KeyInfo(21, KEY_LOCATION_STANDARD, false)
    val LETTER_U = KeyInfo(22, KEY_LOCATION_STANDARD, false)
    val LETTER_I = KeyInfo(23, KEY_LOCATION_STANDARD, false)
    val LETTER_O = KeyInfo(24, KEY_LOCATION_STANDARD, false)
    val LETTER_P = KeyInfo(25, KEY_LOCATION_STANDARD, false)
    val OPEN_BRACKET = KeyInfo(26, KEY_LOCATION_STANDARD, false)
    val CLOSE_BRACKET = KeyInfo(27, KEY_LOCATION_STANDARD, false)
    val BACK_SLASH = KeyInfo(43, KEY_LOCATION_STANDARD, false)

    // 标准键盘第四行
    val CAPS_LOCK = KeyInfo(58, KEY_LOCATION_STANDARD, true)
    val LETTER_A = KeyInfo(30, KEY_LOCATION_STANDARD, false)
    val LETTER_S = KeyInfo(31, KEY_LOCATION_STANDARD, false)
    val LETTER_D = KeyInfo(32, KEY_LOCATION_STANDARD, false)
    val LETTER_F = KeyInfo(33, KEY_LOCATION_STANDARD, false)
    val LETTER_G = KeyInfo(34, KEY_LOCATION_STANDARD, false)
    val LETTER_H = KeyInfo(35, KEY_LOCATION_STANDARD, false)
    val LETTER_J = KeyInfo(36, KEY_LOCATION_STANDARD, false)
    val LETTER_K = KeyInfo(37, KEY_LOCATION_STANDARD, false)
    val LETTER_L = KeyInfo(38, KEY_LOCATION_STANDARD, false)
    val SEMICOLON = KeyInfo(39, KEY_LOCATION_STANDARD, false)
    val QUOTE = KeyInfo(40, KEY_LOCATION_STANDARD, false)
    val ENTER = KeyInfo(28, KEY_LOCATION_STANDARD, false)

    // 标准键盘第五行
    val LEFT_SHIFT = KeyInfo(42, KEY_LOCATION_LEFT, true)
    val LETTER_Z = KeyInfo(44, KEY_LOCATION_STANDARD, false)
    val LETTER_X = KeyInfo(45, KEY_LOCATION_STANDARD, false)
    val LETTER_C = KeyInfo(46, KEY_LOCATION_STANDARD, false)
    val LETTER_V = KeyInfo(47, KEY_LOCATION_STANDARD, false)
    val LETTER_B = KeyInfo(48, KEY_LOCATION_STANDARD, false)
    val LETTER_N = KeyInfo(49, KEY_LOCATION_STANDARD, false)
    val LETTER_M = KeyInfo(50, KEY_LOCATION_STANDARD, false)
    val COMMA = KeyInfo(51, KEY_LOCATION_STANDARD, false)
    val PERIOD = KeyInfo(52, KEY_LOCATION_STANDARD, false)
    val SLASH = KeyInfo(53, KEY_LOCATION_STANDARD, false)
    val RIGHT_SHIFT = KeyInfo(42, KEY_LOCATION_RIGHT, true)

    // 标准键盘第六行
    val LEFT_CTRL = KeyInfo(29, KEY_LOCATION_LEFT, true)
    val RIGHT_CTRL = KeyInfo(29, KEY_LOCATION_RIGHT, true)
    val LEFT_ALT = KeyInfo(56, KEY_LOCATION_LEFT, true)
    val RIGHT_ALT = KeyInfo(56, KEY_LOCATION_RIGHT, true)
    val LEFT_META = KeyInfo(3675, KEY_LOCATION_LEFT, true)
    val RIGHT_META = KeyInfo(3675, KEY_LOCATION_RIGHT, true)
    val SPACE = KeyInfo(57, KEY_LOCATION_STANDARD, false)

    // 上下左右
    val UP = KeyInfo(57416, KEY_LOCATION_NUMPAD, true)
    val DOWN = KeyInfo(57424, KEY_LOCATION_NUMPAD, true)
    val LEFT = KeyInfo(57419, KEY_LOCATION_NUMPAD, true)
    val RIGHT = KeyInfo(57421, KEY_LOCATION_NUMPAD, true)

    // 控制区
    val PRINT_SCREEN = KeyInfo(3639, KEY_LOCATION_STANDARD, true)
    val SCROLL_LOCK = KeyInfo(70, KEY_LOCATION_STANDARD, true)
    val PAUSE = KeyInfo(3653, KEY_LOCATION_STANDARD, true)
    val INSERT = KeyInfo(3666, KEY_LOCATION_NUMPAD, true)
    val DELETE = KeyInfo(3667, KEY_LOCATION_NUMPAD, false)
    val HOME = KeyInfo(3655, KEY_LOCATION_NUMPAD, true)
    val END = KeyInfo(3663, KEY_LOCATION_NUMPAD, true)
    val PAGE_UP = KeyInfo(3657, KEY_LOCATION_NUMPAD, true)
    val PAGE_DOWN = KeyInfo(3665, KEY_LOCATION_NUMPAD, true)

    // 小键盘
    val NUM_LOCK = KeyInfo(69, KEY_LOCATION_NUMPAD, true)
    val NUM_ADD = KeyInfo(3662, KEY_LOCATION_NUMPAD, false)
    val NUM_SUBTRACT = KeyInfo(3658, KEY_LOCATION_NUMPAD, false)
    val NUM_MULTIPLY = KeyInfo(3639, KEY_LOCATION_NUMPAD, false)
    val NUM_DIVIDE = KeyInfo(53, KEY_LOCATION_NUMPAD, false)
    val NUM_ENTER = KeyInfo(28, KEY_LOCATION_NUMPAD, false)
    val NUM_DELETE = KeyInfo(83, KEY_LOCATION_NUMPAD, false)
    val NUM_ISLET_1 = KeyInfo(2, KEY_LOCATION_NUMPAD, false)
    val NUM_ISLET_2 = KeyInfo(3, KEY_LOCATION_NUMPAD, false)
    val NUM_ISLET_3 = KeyInfo(4, KEY_LOCATION_NUMPAD, false)
    val NUM_ISLET_4 = KeyInfo(5, KEY_LOCATION_NUMPAD, false)
    val NUM_ISLET_5 = KeyInfo(6, KEY_LOCATION_NUMPAD, false)
    val NUM_ISLET_6 = KeyInfo(7, KEY_LOCATION_NUMPAD, false)
    val NUM_ISLET_7 = KeyInfo(8, KEY_LOCATION_NUMPAD, false)
    val NUM_ISLET_8 = KeyInfo(9, KEY_LOCATION_NUMPAD, false)
    val NUM_ISLET_9 = KeyInfo(10, KEY_LOCATION_NUMPAD, false)
    val NUM_ISLET_0 = KeyInfo(11, KEY_LOCATION_NUMPAD, false)
  }
}
