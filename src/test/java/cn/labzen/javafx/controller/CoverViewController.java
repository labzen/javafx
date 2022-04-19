package cn.labzen.javafx.controller;

import cn.labzen.javafx.preload.PreloadView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoverViewController extends PreloadView {

  @FXML
  private ProgressBar ppb;
  @FXML
  private Label loadInfoLabel;

  @NotNull
  @Override
  public ProgressBar progressBar() {
    return ppb;
  }

  @Nullable
  @Override
  public Label progressDigestLabel() {
    return loadInfoLabel;
  }
}
