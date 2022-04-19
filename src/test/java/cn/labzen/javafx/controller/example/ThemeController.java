package cn.labzen.javafx.controller.example;

import cn.labzen.javafx.css.CssHandler;
import cn.labzen.javafx.css.MonitorCss;
import cn.labzen.javafx.stage.StageHandler;
import cn.labzen.javafx.theme.ThemeHandler;
import cn.labzen.javafx.view.LabzenView;
import org.jetbrains.annotations.Nullable;

@MonitorCss
public class ThemeController extends LabzenView {

  private static final String[] themes = {"/theme/light",
                                          "/theme/dark"};

  private int theme = 0;

  @Nullable
  @Override
  public String theme() {
    return themes[theme];
  }

  public void goBack() {
    StageHandler.back();
  }

  public void change() {
    theme = theme ^ 1;
    ThemeHandler.reapply(this);
  }

  public void stopCss() {
    CssHandler.suspend();
  }

  public void resumeCss() {
    CssHandler.resume();
  }
}
