package it.unibo.pcd.assignment2.eventdriven.view.tabs

import it.unibo.pcd.assignment2.eventdriven.controller.Controller
import it.unibo.pcd.assignment2.eventdriven.model.TrainInfo
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.Tab

sealed trait TrainTab {
 def displayTrainInfo(trainInfo: TrainInfo): Unit

 def tab: Tab
}

object TrainTab {
  private class StationTabImpl(controller: Controller) extends TrainTab {
    @FXML
    private var root: Tab = _

    private val loader = new FXMLLoader
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource("trainTab.fxml"))
    loader.load

    override val tab: Tab = root

    override def displayTrainInfo(trainInfo: TrainInfo): Unit = ???
  }

  def apply(controller: Controller): TrainTab = new StationTabImpl(controller)
}
