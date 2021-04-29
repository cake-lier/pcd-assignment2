package it.unibo.pcd.assignment2.eventdriven.view.tabs

import it.unibo.pcd.assignment2.eventdriven.controller.Controller
import it.unibo.pcd.assignment2.eventdriven.model.StationInfo
import it.unibo.pcd.assignment2.eventdriven.view.LoadingLabel
import it.unibo.pcd.assignment2.eventdriven.view.cards.TrainBoardCard
import it.unibo.pcd.assignment2.eventdriven.AnyOps.discard
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.{Button, ScrollPane, TextField}
import scalafx.scene.layout.VBox

sealed trait StationTab extends Tab {
 def displayStationInfo(stationInfo: StationInfo): Unit
}

object StationTab {
  import javafx.scene.control.Tab

  private final class StationTabImpl(controller: Controller) extends StationTab {
    private var updatedStation: Option[String] = None
    @FXML
    private var stationName: TextField = new TextField
    @FXML
    private var startMonitorStation: Button = new Button
    @FXML
    private var stopMonitorStation: Button = new Button
    @FXML
    private var arrivals: ScrollPane = new ScrollPane
    @FXML
    private var departures: ScrollPane = new ScrollPane

    val loader = new FXMLLoader
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource("stationTab.fxml"))
    override val tab: Tab = loader.load[Tab]
    startMonitorStation.setOnMouseClicked(_ => {
      startMonitorStation.setDisable(true)
      arrivals.setContent(LoadingLabel().label)
      departures.setContent(LoadingLabel().label)
      val station = stationName.getText
      controller.startStationInfoUpdates(station)
      updatedStation = Some(station)
      stopMonitorStation.setDisable(false)
    })
    stopMonitorStation.setOnMouseClicked(_ => {
      stopMonitorStation.setDisable(true)
      updatedStation.foreach(controller.stopStationInfoUpdates)
      updatedStation = None
      startMonitorStation.setDisable(false)
    })

    override def displayStationInfo(stationInfo: StationInfo): Unit = {
      val arrivalsContainer = new VBox(5)
      arrivals.setContent(arrivalsContainer)
      discard {
        arrivalsContainer.children ++= stationInfo.arrivals.map(r => TrainBoardCard(r, TrainBoardCard.Type.Arrival)).map(_.pane)
      }
      val departuresContainer = new VBox(5)
      departures.setContent(departuresContainer)
      discard {
        departuresContainer.children ++= stationInfo.departures
                                                    .map(r => TrainBoardCard(r, TrainBoardCard.Type.Departure))
                                                    .map(_.pane)
      }
    }
  }

  def apply(controller: Controller): StationTab = new StationTabImpl(controller)
}
