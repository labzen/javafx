package cn.labzen.javafx.init;

import cn.labzen.javafx.initialize.LabzenApplicationInitializer;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class DemoInit2 extends LabzenApplicationInitializer {

  @Override
  public int weight() {
    return 8;
  }

  @Override
  public int order() {
    return 2;
  }

  @Nullable
  @Override
  public String finishedMessage() {
    return "正常的结束了";
  }

  @Override
  public void run() {
    try {
      Thread.sleep(2500);
      info("中间来点儿信息", 0.2);
      Thread.sleep(500);
      info("中间再来点儿信息...", 0.3);
      Thread.sleep(500);
      info("中间再来点儿信息1...", 0.1);
      Thread.sleep(500);
      info("中间再来点儿信息22...", 0.2);
      Thread.sleep(500);
      info("中间再来点儿信息22...", 0.1);
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Nullable
  @Override
  public String startMessage() {
    return "开始了哈，这个时间长";
  }
}
