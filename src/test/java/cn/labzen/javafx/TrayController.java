package cn.labzen.javafx;

import cn.labzen.javafx.component.tray.AbstractTrayMenuController;
import cn.labzen.javafx.component.tray.TrayItem;
import org.springframework.stereotype.Component;

@Component
public class TrayController extends AbstractTrayMenuController {

  @TrayItem(name = "console", text = "控制台打印", group = 0, order = 1, selectable = true)
  public void consolePrint(boolean checked) {
    System.out.println("in handler --->  consolePrint >>> " + checked);
  }

  @TrayItem(name = "close", text = "退出", group = 0, order = 0)
  public void exit() {
    LabzenJavaFX.terminate();
  }

  @TrayItem(name = "aw", text = "弹窗", group = 1, order = 0)
  public void alertWindow() {
    System.out.println("in handler --->  alertWindow");
  }

  @TrayItem(name = "ae", text = "弹框", group = 1, order = 1)
  public void alertElement() {
    System.out.println("in handler --->  alertElement");
  }

  @TrayItem(name = "other/", text = "其他的功能挺长的反正", group = 2)
  public void other() {
    System.out.println("in handler --->  other");
  }

  @TrayItem(name = "other/fun1", text = "功能")
  public void otherFun1() {
    System.out.println("in handler --->  otherFun1");
  }

  @TrayItem(name = "other/fun2", text = "功能2", order = 1, selectable = true)
  public void otherFun2(boolean checked) {
    System.out.println("in handler --->  otherFun2 >>>> " + checked);
  }

  @TrayItem(name = "other/fun3", text = "功能", group = 1)
  public void otherFun3() {
    System.out.println("in handler --->  otherFun3");
  }
}
