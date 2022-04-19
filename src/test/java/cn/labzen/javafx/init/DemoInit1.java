package cn.labzen.javafx.init;

import cn.labzen.javafx.initialize.LabzenApplicationInitializer;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class DemoInit1 extends LabzenApplicationInitializer {

  @Override
  public int weight() {
    return 1;
  }

  @Nullable
  @Override
  public String finishedMessage() {
    return "正正常常";
  }

  @Override
  public void run() {
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
