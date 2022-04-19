package cn.labzen.javafx.controller;

import cn.labzen.javafx.stage.StageHandler;
import cn.labzen.javafx.view.LabzenView;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class AppViewController extends LabzenView {

  @FXML
  private Pane root;

  public void toA() {
    StageHandler.go("sub/va");
  }

  public void goDialogAlertWindow() {
    StageHandler.go("example/alert_window");
  }

  public void goDialogAlertElement() {
    StageHandler.go("example/alert_element");
  }

  public void goTheme() {
    StageHandler.go("example/theme");
  }
}
