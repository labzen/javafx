package cn.labzen.javafx.controller;

import cn.labzen.javafx.stage.AboutStage;
import cn.labzen.javafx.stage.LabzenStage;
import cn.labzen.javafx.stage.LabzenStageContainer;
import cn.labzen.javafx.stage.StageHandler;
import cn.labzen.javafx.view.LabzenView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

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

  public void showAbout(ActionEvent actionEvent) {
    LabzenStage stage = StageHandler.createStage(AboutStage.class);
    LabzenStageContainer originalStage = StageHandler.changePrimaryStage(stage);

    Stage ops = originalStage.instance();
    ops.close();

    stage.showAndCenterOnScreen();
  }
}
