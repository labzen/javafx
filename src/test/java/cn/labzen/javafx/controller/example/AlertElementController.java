package cn.labzen.javafx.controller.example;

import cn.labzen.javafx.animation.AnimationType;
import cn.labzen.javafx.dialog.DialogElement;
import cn.labzen.javafx.dialog.DialogElementBuilder;
import cn.labzen.javafx.dialog.DialogElementContainer;
import cn.labzen.javafx.dialog.DialogHandler;
import cn.labzen.javafx.stage.StageHandler;
import cn.labzen.javafx.view.LabzenView;
import cn.labzen.javafx.view.ViewHandler;
import cn.labzen.javafx.view.ViewWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AlertElementController extends LabzenView implements DialogElementContainer {

  @FXML
  private StackPane root;
  @FXML
  private ComboBox<String> animation;
  @FXML
  private TextField speed;
  @FXML
  private TextField delay;
  @FXML
  private TextField closeDelay;
  @FXML
  private CheckBox overlayClose;
  @FXML
  private ColorPicker colorPick;
  @FXML
  private Label eventDetails;
  @FXML
  private ComboBox<String> pos;
  @FXML
  private TextField ml;
  @FXML
  private TextField mr;
  @FXML
  private TextField mt;
  @FXML
  private TextField mb;

  @Override
  public void initialize(@Nullable URL location, @Nullable ResourceBundle resources) {
    List<String> ats = Arrays.stream(AnimationType.values()).map(Enum::toString).collect(Collectors.toList());
    ObservableList<String> animationTypes = FXCollections.observableArrayList(ats);
    animation.setItems(animationTypes);
    animation.getSelectionModel().selectFirst();

    List<String> pts = Arrays.stream(Pos.values()).map(Enum::toString).collect(Collectors.toList());
    ObservableList<String> locationPos = FXCollections.observableArrayList(pts);
    pos.setItems(locationPos);
    pos.getSelectionModel().select(Pos.CENTER.toString());
  }

  public void goBack() {
    StageHandler.back();
  }

  public void showDialog() {
    ViewWrapper wrapper = ViewHandler.loadView("example/content/default");

    DialogElementBuilder deb = DialogHandler.createElementBuilder().container(this).content(wrapper);
    if (overlayClose.isSelected()) {
      deb.closeByOverlay();
    }

    String selectedItem = animation.getSelectionModel().getSelectedItem();
    AnimationType animationType = AnimationType.valueOf(selectedItem);
    if (animationType != AnimationType.NONE) {
      deb.showWithAnimation(animationType, Double.valueOf(speed.getText()), Integer.valueOf(delay.getText()));
    }
    Color bc = colorPick.getValue();
    if (!Color.BLACK.equals(bc)) {
      deb.overlayColor(bc);
    }
    Pos position = Pos.valueOf(this.pos.getSelectionModel().getSelectedItem());
    Insets margin = new Insets(Double.parseDouble(mt.getText()),
        Double.parseDouble(mr.getText()),
        Double.parseDouble(mb.getText()),
        Double.parseDouble(ml.getText()));
    deb.position(position, margin);
    DialogElement de = deb.build();
    de.show();

    int cd = Integer.parseInt(closeDelay.getText());
    if (cd > 0) {
      de.close(cd);
    }
  }

  @Override
  public void dialogOpened() {
    eventDetails.setText("opened -> 弹框已弹出");
  }

  @Override
  public void dialogClosed() {
    eventDetails.setText("closed -> 弹框已关闭");
  }
}
