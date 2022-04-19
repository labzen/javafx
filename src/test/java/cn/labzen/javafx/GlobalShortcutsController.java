package cn.labzen.javafx;

import cn.labzen.javafx.component.shortcuts.KeyCombine;
import cn.labzen.javafx.component.shortcuts.Keys;
import cn.labzen.javafx.component.shortcuts.ShortcutsController;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GlobalShortcutsController implements ShortcutsController {

  @NotNull
  @Override
  public Map<KeyCombine, String> registerKey() {
    Map<KeyCombine, String> keys = new HashMap<>();

    var k = Keys.Companion;
    var ctrl = k.getLEFT_CTRL().signature();
    var alt = k.getLEFT_ALT().signature();
    keys.put(new KeyCombine(ctrl, alt, k.getLETTER_L().signature()), "test1");
    keys.put(new KeyCombine(ctrl, k.getLETTER_U().signature()), "test2");
    keys.put(new KeyCombine(ctrl, k.getLETTER_B().signature()), "test3");

    return keys;
  }

  public void test1() {
    System.out.println("in method: test1 >>> 11111111111");
  }

  public void test2() {
    System.out.println("in method: test2 >>> 2222222222222");
  }

  public void test3() {
    System.out.println("in method: test3 >>> 33333333333");
  }
}
