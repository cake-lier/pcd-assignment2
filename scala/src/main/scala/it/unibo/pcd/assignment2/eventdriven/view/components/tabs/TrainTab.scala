package it.unibo.pcd.assignment2.eventdriven.view.components.tabs

import it.unibo.pcd.assignment2.eventdriven.model.TrainInfo
import it.unibo.pcd.assignment2.eventdriven.view.components.Component
import javafx.scene.control.Tab
import scalafx.scene.layout.Pane

sealed trait TrainTab extends Component[Tab] {
  def displayTrainInfo(trainInfo: TrainInfo): Unit

  def suspendTrainMonitoring(): Unit
}

object TrainTab {
  import it.unibo.pcd.assignment2.eventdriven.AnyOps.{discard, AnyOps}
  import it.unibo.pcd.assignment2.eventdriven.controller.Controller
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component.AbstractComponent
  import it.unibo.pcd.assignment2.eventdriven.model.{RouteStation, TrainInfo}
  import it.unibo.pcd.assignment2.eventdriven.model.TravelState._
  import it.unibo.pcd.assignment2.eventdriven.view.components.cards.StopCard
  import it.unibo.pcd.assignment2.eventdriven.view.components.LoadingLabel
  import javafx.fxml.FXML
  import javafx.scene.control.{Button, ScrollPane, TextField}
  import scalafx.geometry.Insets
  import scalafx.scene.control.Label
  import scalafx.scene.layout.VBox

  private final class TrainTabImpl(controller: Controller) extends AbstractComponent[Tab]("trainTab.fxml") with TrainTab {
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
      setLabelForText(s"${trainInfo.train.trainType.toString} ${trainInfo.train.trainCode.getOrElse("")}", container)
      setLabelForText(getTextForState(trainInfo.stations), container)
      discard { container.children ++= trainInfo.stations.map(StopCard(_)) }
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

    private def getTextForState(stations: List[RouteStation]): String = {
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

  def apply(controller: Controller): TrainTab = new TrainTabImpl(controller)
}
