package cn.labzen.javafx.controller;

import cn.labzen.javafx.view.LabzenView;
import cn.labzen.logger.Loggers;
import org.slf4j.Logger;

public class Sub2ViewController extends LabzenView {

  private final Logger logger = Loggers.getLogger(Sub2ViewController.class);

  @Override
  public void dispose() {
    logger.info("子视图从场景中被移除（未销毁） ==> {}", this);
  }

  @Override
  public void destroy() {
    logger.info("子视图这次真被销毁了 ==> {}", this);
  }
}
