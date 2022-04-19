package cn.labzen.javafx.init;

import cn.labzen.javafx.initialize.LabzenApplicationInitializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class DemoInitError extends LabzenApplicationInitializer {

  @Override
  public int weight() {
    return 2;
  }

  @Nullable
  @Override
  public String finishedMessage() {
    return "因为出错了，这个不大应该出现吧";
  }

  @Nullable
  @Override
  public String startMessage() {
    return "这是要出错的那个";
  }

  @Override
  public void run() {
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    throw new IllegalStateException("反正就是报错了，别纠结为啥");
  }

  @Override
  public boolean exitIfException(@NotNull Throwable throwable) {
    return false;
  }
}
