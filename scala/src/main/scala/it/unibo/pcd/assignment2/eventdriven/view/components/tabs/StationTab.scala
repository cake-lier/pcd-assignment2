package it.unibo.pcd.assignment2.eventdriven.view.components.tabs

import it.unibo.pcd.assignment2.eventdriven.model.StationInfo
import it.unibo.pcd.assignment2.eventdriven.view.components.Component
import javafx.scene.control.Tab

/** A tab [[Component]] for displaying real-time information about [[it.unibo.pcd.assignment2.eventdriven.model.Station]]s.
 *
 *  This [[Component]] displays the controls for getting and suspend the retrieval of real-time information about one
 *  [[it.unibo.pcd.assignment2.eventdriven.model.Station]] at a time through [[StationInfo]] objects. The View component
 *  delegates all those responsibilities to this [[Component]] so as to better encapsulate its behavior.
 *  The instantiation is made through its companion object.
 */
sealed trait StationTab extends Component[Tab] {
  /** Displays a new [[StationInfo]] information in the GUI.
   *
   *  @param stationInfo the [[StationInfo]] to display
   */
  def displayStationInfo(stationInfo: StationInfo): Unit

  /** Displays the monitoring of [[StationInfo]]s as suspended. */
  def suspendStationMonitoring(): Unit
}

/** Factory for creating new instances of [[StationTab]]. */
object StationTab {
  import it.unibo.pcd.assignment2.eventdriven.controller.Controller
  import it.unibo.pcd.assignment2.eventdriven.model.StationInfo
  import it.unibo.pcd.assignment2.eventdriven.view.components.cards.TrainBoardCard
  import it.unibo.pcd.assignment2.eventdriven.AnyOps.discard
  import javafx.fxml.FXML
  import javafx.scene.control.{Button, ScrollPane, TextField}
  import scalafx.scene.layout.{VBox, Pane}
  import Component.componentToInner
  import it.unibo.pcd.assignment2.eventdriven.view.components.LoadingLabel
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component.AbstractComponent

  /* Implementation of the StationTab Component. */
  private final class StationTabImpl(controller: Controller) extends AbstractComponent[Tab](fxmlFileName =  "stationTab.fxml")
    with StationTab {
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
      val spacing = 5
      val arrivalsContainer = new VBox(spacing)
      arrivals.setContent(arrivalsContainer)
      discard {
        arrivalsContainer.children ++= stationInfo.arrivals.map(r => TrainBoardCard(r, TrainBoardCard.Type.Arrival))
      }
      val departuresContainer = new VBox(spacing)
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

  /** Creates a new instance of [[StationTab]].
   *
   *  @param controller the [[Controller]] component used for the needed interactions with the backend of this application
   *  @return a new instance of [[StationTab]]
   */
  def apply(controller: Controller): StationTab = new StationTabImpl(controller)
}
