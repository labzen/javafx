package cn.labzen.javafx.controller.example.content;

import cn.labzen.javafx.animation.AnimationType;
import cn.labzen.javafx.view.LabzenView;

public class DefaultContent extends LabzenView {

  public void closeMe() {
    closeDialog();
  }

  public void login() {
    animate(AnimationType.TADA, 1.5);
  }

}
