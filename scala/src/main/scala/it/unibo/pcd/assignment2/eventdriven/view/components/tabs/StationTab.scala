package it.unibo.pcd.assignment2.eventdriven.view.components.tabs

import it.unibo.pcd.assignment2.eventdriven.model.StationInfo
import it.unibo.pcd.assignment2.eventdriven.view.components.Component.AbstractComponent
import it.unibo.pcd.assignment2.eventdriven.view.components.Component
import javafx.scene.control.Tab
import scalafx.scene.layout.Pane

sealed trait StationTab extends Component[Tab] {
  def displayStationInfo(stationInfo: StationInfo): Unit

  def suspendStationMonitoring(): Unit
}

object StationTab {
  import it.unibo.pcd.assignment2.eventdriven.controller.Controller
  import it.unibo.pcd.assignment2.eventdriven.model.StationInfo
  import it.unibo.pcd.assignment2.eventdriven.view.components.cards.TrainBoardCard
  import it.unibo.pcd.assignment2.eventdriven.AnyOps.discard
  import javafx.fxml.FXML
  import javafx.scene.control.{Button, ScrollPane, TextField}
  import scalafx.scene.layout.VBox
  import Component.componentToInner
  import it.unibo.pcd.assignment2.eventdriven.view.components.LoadingLabel

  private final class StationTabImpl(controller: Controller) extends AbstractComponent[Tab]("stationTab.fxml") with StationTab {
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

    override val inner: Tab = loader.load[Tab]
    startMonitorStation.setOnMouseClicked(_ => {
      startMonitorStation.setDisable(true)
      arrivals.setContent(LoadingLabel().inner)
      departures.setContent(LoadingLabel().inner)
      val station = stationName.getText
      controller.startStationInfoUpdates(station)
      updatedStation = Some(station)
      stopMonitorStation.setDisable(false)
    })
    stopMonitorStation.setOnMouseClicked(_ => {
      updatedStation.foreach(controller.stopStationInfoUpdates)
      suspendStationMonitoring()
    })

    override def displayStationInfo(stationInfo: StationInfo): Unit = {
      val arrivalsContainer = new VBox(5)
      arrivals.setContent(arrivalsContainer)
      discard {
        arrivalsContainer.children ++= stationInfo.arrivals.map(r => TrainBoardCard(r, TrainBoardCard.Type.Arrival))
      }
      val departuresContainer = new VBox(5)
      departures.setContent(departuresContainer)
      discard {
        departuresContainer.children ++= stationInfo.departures.map(r => TrainBoardCard(r, TrainBoardCard.Type.Departure))
      }
    }

    override def suspendStationMonitoring(): Unit = {
      stopMonitorStation.setDisable(true)
      updatedStation = None
      departures.setContent(new Pane)
      startMonitorStation.setDisable(false)
      arrivals.setContent(new Pane)
    }
  }

  def apply(controller: Controller): StationTab = new StationTabImpl(controller)
}
