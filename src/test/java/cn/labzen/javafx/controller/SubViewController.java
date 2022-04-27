package cn.labzen.javafx.controller;

import cn.labzen.cells.core.utils.Randoms;
import cn.labzen.javafx.view.LabzenView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SubViewController extends LabzenView {

  @FXML
  private Label abc;

  public void change(ActionEvent actionEvent) {
    abc.textProperty().setValue(Randoms.string(5));
  }
}
