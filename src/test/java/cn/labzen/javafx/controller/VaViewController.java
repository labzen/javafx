package cn.labzen.javafx.controller;

import cn.labzen.javafx.stage.StageHandler;
import cn.labzen.javafx.view.LabzenView;

public class VaViewController extends LabzenView {

  public void toB() {
    StageHandler.go("sub/vb");
  }

  public void backApp() {
    StageHandler.back();
  }
}
