package cn.labzen.javafx.controller.example;

import cn.labzen.cells.core.utils.Strings;
import cn.labzen.javafx.dialog.DialogHandler;
import cn.labzen.javafx.dialog.DialogWindowBuilder;
import cn.labzen.javafx.stage.StageHandler;
import cn.labzen.javafx.view.LabzenView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class AlertWindowController extends LabzenView {

  @FXML
  private CheckBox hideTitle;
  @FXML
  private CheckBox showTask;
  @FXML
  private CheckBox modal;
  @FXML
  private CheckBox shutdown;
  @FXML
  private ComboBox<String> type;
  @FXML
  private TextField title;
  @FXML
  private TextField header;
  @FXML
  private TextField content;
  @FXML
  private Label console;

  @Override
  public void initialize(@Nullable URL location, @Nullable ResourceBundle resources) {
    ObservableList<String> types = FXCollections.observableArrayList("info", "warn", "error", "confirm");
    type.setItems(types);
    type.getSelectionModel().selectFirst();
  }

  public void goBack() {
    StageHandler.back();
  }

  public void show() {
    DialogWindowBuilder db = DialogHandler.createWindowBuilder();

    if (hideTitle.isSelected()) {
      db.hideTitleBar();
    }
    if (showTask.isSelected()) {
      db.showTaskBar();
    }
    if (!modal.isSelected()) {
      db.dontModal();
    }
    if (shutdown.isSelected()) {
      db.shutdownWhenDialogClosed();
    }
    String t = title.getText();
    if (!Strings.isBlank(t)) {
      db.title(t);
    }

    String h = Strings.blankToNull(header.getText());
    String c = content.getText();

    String at = type.getSelectionModel().getSelectedItem();
    switch (at) {
      case "info": {
        db.information(c, h);
        break;
      }
      case "warn": {
        db.warning(c, h);
        break;
      }
      case "error": {
        db.error(c, h);
        break;
      }
      case "confirm": {
        db.confirmation(Strings.nullTo(h, ""), c);
        break;
      }
    }

    db.show();
  }

  public void login() {
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(-10, 0, 0, 20));

    TextField username = new TextField();
    username.setPromptText("Username");
    PasswordField password = new PasswordField();
    password.setPromptText("Password");

    grid.add(new Label("Username:"), 0, 0);
    grid.add(username, 1, 0);
    grid.add(new Label("Password:"), 0, 1);
    grid.add(password, 1, 1);

    DialogWindowBuilder db = DialogHandler.createWindowBuilder();
    db.confirmation("用户登录");
    db.alertGraphic("/image/login.png");
    db.expandContent(grid);
    ButtonType loginButton = new ButtonType("登录", ButtonBar.ButtonData.OK_DONE);
    db.customButton(loginButton, wrapper -> {
      Button button = wrapper.getButton();
      button.setDisable(true);
      username.textProperty().addListener((observable, oldValue, newValue) -> {
        button.setDisable(newValue.isBlank());
      });
    });
    db.customButton("重置", button -> {
      button.on(event -> {
        username.textProperty().set("");
        password.textProperty().set("");
        return false;
      });
    });
    db.show((text, type) -> {
      if (type == loginButton) {
        console.setText("用户登录：username -> " + username.getText() + " ; password -> " + password.getText());
      }
    });
  }

  public void exception() {
    Exception parent = new RuntimeException("parent exception");
    Exception ex = new IllegalStateException("illegal state exception", parent);
    DialogWindowBuilder db = DialogHandler.createWindowBuilder();
    if (shutdown.isSelected()) {
      db.error("===> exception details is:", ex, "确定吧，确定就退出了").shutdownWhenDialogClosed();
    } else {
      db.error("===> exception details is:", ex);
    }
    db.show();
  }

  public void multiButtons() {
    AtomicReference<Button> closeButton = new AtomicReference<>();
    DialogHandler.createWindowBuilder()
                 .information("霹雳吧啦的挺多信息", "大标题")
                 .customButton("没啥用")
                 .customButton(new ButtonType("左边孤独", ButtonBar.ButtonData.HELP_2))
                 .customButton("你点一个试试", wrapper -> {
                   wrapper.on(event -> {
                     console.setText("试试就试试");
                     Button button = closeButton.get();
                     if (button.isDisable()) {
                       button.setDisable(false);
                       button.setText("点我有惊喜");
                     }
                     return false;
                   });
                 })
                 .customButton(new ButtonType("我没用", ButtonBar.ButtonData.RIGHT), wrapper -> {
                   Button button = wrapper.getButton();
                   closeButton.set(button);
                   button.setDisable(true);
                 })
                 .withoutDefaultCancelButton()
                 .show((text, type) -> {
                   if (type.getButtonData() == ButtonBar.ButtonData.RIGHT) {
                     console.setText("哪有什么惊喜，这也信");
                   }
                 });
  }
}
