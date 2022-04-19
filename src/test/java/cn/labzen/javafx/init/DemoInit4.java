package cn.labzen.javafx.init;

import cn.labzen.javafx.initialize.LabzenApplicationInitializer;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class DemoInit4 extends LabzenApplicationInitializer {

  @Override
  public int weight() {
    return 4;
  }

  @Nullable
  @Override
  public String finishedMessage() {
    return "就这些吧";
  }

  @Override
  public void run() {
    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
