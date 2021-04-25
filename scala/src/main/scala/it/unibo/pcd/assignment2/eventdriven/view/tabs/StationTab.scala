package it.unibo.pcd.assignment2.eventdriven.view.tabs

import it.unibo.pcd.assignment2.eventdriven.controller.Controller
import it.unibo.pcd.assignment2.eventdriven.model.StationInfo
import it.unibo.pcd.assignment2.eventdriven.view.LoadingLabel
import it.unibo.pcd.assignment2.eventdriven.view.cards.{TrainBoardCard, TrainBoardCardType}
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.{Button, Tab, TitledPane}
import scalafx.application.Platform
import javafx.scene.control.TextField
import scalafx.scene.layout.VBox

sealed trait StationTab {
 def displayStationInfo(stationInfo: StationInfo): Unit

 def tab: Tab
}

object StationTab {
  private class StationTabImpl(controller: Controller) extends StationTab {
    private var updatedStation: Option[String] = None
    @FXML
    private var root: Tab = _
    @FXML
    private var stationName: TextField = _
    @FXML
    private var startMonitorStation: Button = _
    @FXML
    private var stopMonitorStation: Button = _
    @FXML
    private var arrivals: TitledPane = _
    @FXML
    private var departures: TitledPane = _

    val loader = new FXMLLoader
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource("stationTab.fxml"))
    loader.load
    startMonitorStation.setOnMouseClicked(_ => {
      arrivals.setContent(LoadingLabel().label)
      departures.setContent(LoadingLabel().label)
      val station = stationName.getText
      controller.startStationInfoUpdates(station)
      updatedStation = Some(station)
    })
    stopMonitorStation.setOnMouseClicked(_ => updatedStation.foreach(controller.stopStationInfoUpdates))

    override val tab: Tab = root

    override def displayStationInfo(stationInfo: StationInfo): Unit = Platform.runLater({
      val arrivalsContainer = new VBox(5)
      arrivals.setContent(arrivalsContainer)
      arrivalsContainer.children
                       .setAll(stationInfo.arrivals.map(r => TrainBoardCard(r, TrainBoardCardType.ARRIVAL)).map(_.pane).toSeq: _*)
      val departuresContainer = new VBox(5)
      departures.setContent(departuresContainer)
      departuresContainer.children
                         .setAll(stationInfo.departures.map(r => TrainBoardCard(r, TrainBoardCardType.DEPARTURE)).map(_.pane)
                             .toSeq: _*)
    })
  }

  def apply(controller: Controller): StationTab = new StationTabImpl(controller)
}
