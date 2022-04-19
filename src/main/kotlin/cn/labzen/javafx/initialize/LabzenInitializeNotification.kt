package cn.labzen.javafx.initialize

import javafx.application.Preloader

abstract class LabzenInitializeNotification(
  val initializer: Class<out LabzenApplicationInitializer>
) : Preloader.PreloaderNotification {

  abstract fun notification(): String
}
