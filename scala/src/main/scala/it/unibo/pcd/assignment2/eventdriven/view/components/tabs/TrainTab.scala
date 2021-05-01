package it.unibo.pcd.assignment2.eventdriven.view.components.tabs

import it.unibo.pcd.assignment2.eventdriven.model.TrainInfo
import it.unibo.pcd.assignment2.eventdriven.view.components.Component
import javafx.scene.control.Tab

/** A tab [[Component]] for displaying real-time information about [[it.unibo.pcd.assignment2.eventdriven.model.Train]]s.
 *
 *  This [[Component]] displays the controls for getting and suspend the retrieval of real-time information about one
 *  [[it.unibo.pcd.assignment2.eventdriven.model.Train]] at a time through [[TrainInfo]] objects. The View component
 *  delegates all those responsibilities to this [[Component]] so as to better encapsulate its behavior.
 *  The instantiation is made through its companion object.
 */
sealed trait TrainTab extends Component[Tab] {
  /** Displays a new [[TrainInfo]] information in the GUI.
   *
   *  @param trainInfo the [[TrainInfo]] to display
   */
  def displayTrainInfo(trainInfo: TrainInfo): Unit

  /** Displays the monitoring of [[TrainInfo]]s as suspended. */
  def suspendTrainMonitoring(): Unit
}

/** Factory for creating new instances of [[TrainTab]]. */
object TrainTab {
  import it.unibo.pcd.assignment2.eventdriven.AnyOps.{discard, AnyOps}
  import it.unibo.pcd.assignment2.eventdriven.controller.Controller
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component.AbstractComponent
  import it.unibo.pcd.assignment2.eventdriven.model.{Stop, TrainInfo}
  import it.unibo.pcd.assignment2.eventdriven.model.TravelState._
  import it.unibo.pcd.assignment2.eventdriven.view.components.cards.StopCard
  import it.unibo.pcd.assignment2.eventdriven.view.components.LoadingLabel
  import javafx.fxml.FXML
  import javafx.scene.control.{Button, ScrollPane, TextField}
  import scalafx.geometry.Insets
  import scalafx.scene.control.Label
  import scalafx.scene.layout.{VBox, Pane}

  /* Implementation of the SolutionTab Component. */
  private final class TrainTabImpl(controller: Controller) extends AbstractComponent[Tab](fxmlFileName =  "trainTab.fxml")
    with TrainTab {
    private var updatedTrain: Option[String] = None
    @FXML
    private var trainCode: TextField = new TextField
    @FXML
    private var startMonitorTrain: Button = new Button
    @FXML
    private var stopMonitorTrain: Button = new Button
    @FXML
    private var stations: ScrollPane = new ScrollPane

    override val inner: Tab = loader.load[Tab]

    stations.setPadding(Insets(0, 5.0, 0, 5.0))
    startMonitorTrain.setOnMouseClicked(_ => {
      startMonitorTrain.setDisable(true)
      stations.setContent(LoadingLabel().inner)
      val station = trainCode.getText
      controller.startTrainInfoUpdates(station)
      updatedTrain = Some(station)
      stopMonitorTrain.setDisable(false)
    })
    stopMonitorTrain.setOnMouseClicked(_ => {
      updatedTrain.foreach(controller.stopTrainInfoUpdates)
      suspendTrainMonitoring()
    })

    override def displayTrainInfo(trainInfo: TrainInfo): Unit = {
      val container = new VBox(5)
      container.padding = Insets(0, 0, 5.0, 0)
      setLabelForText(s"${trainInfo.route.trainType.toString} ${trainInfo.route.trainCode.getOrElse("")}", container)
      setLabelForText(getTextForState(trainInfo.stops), container)
      discard { container.children ++= trainInfo.stops.map(StopCard(_)) }
      stations.setContent(container)
    }

    override def suspendTrainMonitoring(): Unit = {
      stopMonitorTrain.setDisable(true)
      updatedTrain = None
      startMonitorTrain.setDisable(false)
      stations.setContent(new Pane)
    }

    private def setLabelForText(text: String, container: VBox): Unit = {
      val label = new Label(text)
      label.maxWidth = Double.MaxValue
      label.maxHeight = Double.MaxValue
      label.margin = Insets(5.0, 0, 0, 0)
      label.styleClass = List("gridCell")
      label.applyCss()
      discard { container.children += label }
    }

    private def getTextForState(stations: List[Stop]): String = {
      if (stations.headOption.exists(_.actualDepartureDatetime.isEmpty)) {
        "Il treno non è ancora partito"
      }
      else if (stations.lastOption.exists(_.actualArrivalDatetime.isDefined)) {
        "Il treno è già arrivato"
      }
      else {
        stations.map(s => (s.departureState, s.arrivalState))
                .findLast(p => p._1 =/= Nothing || p._2 =/= Nothing)
                .map(p => Some(p._2).filter(_ =/= Nothing).getOrElse(p._1))
                .map({
                  case InTime => "Il treno è in orario"
                  case Delayed(m) => s"Il treno è in ritardo di ${m.toString} minuti"
                  case Early(m) => s"Il treno è in anticipo di ${m.toString} minuti"
                  case _ => "--"
                })
                .getOrElse("--")
      }
    }
  }

  /** Creates a new instance of [[TrainTab]].
   *
   *  @param controller the [[Controller]] component used for the needed interactions with the backend of this application
   *  @return a new instance of [[TrainTab]]
   */
  def apply(controller: Controller): TrainTab = new TrainTabImpl(controller)
}
