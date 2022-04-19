package cn.labzen.javafx.init;

import cn.labzen.javafx.initialize.LabzenApplicationInitializer;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class DemoInit3 extends LabzenApplicationInitializer {

  @Override
  public int weight() {
    return 2;
  }

  @Nullable
  @Override
  public String finishedMessage() {
    return "第四个了哈，是我叫3";
  }

  @Override
  public void run() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
