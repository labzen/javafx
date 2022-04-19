package cn.labzen.javafx.controller;

import cn.labzen.javafx.stage.StageHandler;
import cn.labzen.javafx.view.LabzenView;
import cn.labzen.javafx.view.ViewHandler;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import org.jetbrains.annotations.Nullable;

public class VbViewController extends LabzenView {

  @FXML
  private SubScene innerPane;

  private int index = 1;

  public void toC() {
    StageHandler.go("sub/vc");
  }

  public void backApp() {
    StageHandler.back(null, "AppView");
  }

  public void backA() {
    StageHandler.back();
  }

  @Nullable
  @Override
  public SubScene partialViewContainerNode(@Nullable String id) {
    return innerPane;
  }

  public void show() {
    ViewHandler.go(id(), "sub/vb1");
  }

  public void next() {
    if (index >= 3) {
      return;
    }
    ViewHandler.go(id(), "sub/vb" + ++index);
  }

  public void prev() {
    if (index <= 1) {
      return;
    }
    ViewHandler.back(id(), null, null, "sub/vb" + --index);
  }
}
