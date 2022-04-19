package cn.labzen.javafx.controller;

import cn.labzen.javafx.stage.StageHandler;
import cn.labzen.javafx.view.LabzenView;
import org.jetbrains.annotations.Nullable;

public class VcViewController extends LabzenView {

  @Nullable
  @Override
  public String theme() {
    return "/theme/dark";
  }

  public void backApp() {
    StageHandler.back(null, "AppView");
  }

  public void backA() {
    StageHandler.back(null, "");
  }

  public void backB() {
    StageHandler.back();
  }
}
