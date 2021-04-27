package it.unibo.pcd.assignment2.eventdriven.view.tabs

import it.unibo.pcd.assignment2.eventdriven.controller.Controller
import it.unibo.pcd.assignment2.eventdriven.model.StationInfo
import it.unibo.pcd.assignment2.eventdriven.view.LoadingLabel
import it.unibo.pcd.assignment2.eventdriven.view.cards.TrainBoardCard
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.{Button, ScrollPane, TextField}
import scalafx.scene.layout.VBox

sealed trait StationTab extends Tab {
 def displayStationInfo(stationInfo: StationInfo): Unit
}

object StationTab {
  import javafx.scene.control.Tab

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
    private var arrivals: ScrollPane = _
    @FXML
    private var departures: ScrollPane = _

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
    stopMonitorStation.setOnMouseClicked(_ => {
      updatedStation.foreach(controller.stopStationInfoUpdates)
      updatedStation = None
    })

    override val tab: Tab = root

    override def displayStationInfo(stationInfo: StationInfo): Unit = {
      val arrivalsContainer = new VBox(5)
      arrivals.setContent(arrivalsContainer)
      arrivalsContainer.children
                       .setAll(stationInfo.arrivals
                                          .map(r => TrainBoardCard(r, TrainBoardCard.Type.ARRIVAL))
                                          .map(_.pane)
                                          .toSeq: _*)
      val departuresContainer = new VBox(5)
      departures.setContent(departuresContainer)
      departuresContainer.children
                         .setAll(stationInfo.departures.map(r => TrainBoardCard(r, TrainBoardCard.Type.DEPARTURE))
                                                       .map(_.pane)
                                                       .toSeq: _*)
    }
  }

  def apply(controller: Controller): StationTab = new StationTabImpl(controller)
}
