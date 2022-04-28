package cn.labzen.javafx.controller;

import cn.labzen.cells.core.utils.Randoms;
import cn.labzen.javafx.view.LabzenView;
import cn.labzen.logger.Loggers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.slf4j.Logger;

public class Sub3ViewController extends LabzenView {

  private final Logger logger = Loggers.getLogger(Sub3ViewController.class);

  @FXML
  private Label abc;

  public void change(ActionEvent actionEvent) {
    abc.textProperty().setValue(Randoms.string(5));
  }

  @Override
  public void destroy() {
    logger.info("子视图被销毁 ==> {}", this);
  }
}
