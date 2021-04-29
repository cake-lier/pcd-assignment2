package it.unibo.pcd.assignment2.eventdriven.view.tabs

import it.unibo.pcd.assignment2.eventdriven.AnyOps.{discard, AnyOps}
import it.unibo.pcd.assignment2.eventdriven.controller.Controller
import it.unibo.pcd.assignment2.eventdriven.model.TravelState._
import it.unibo.pcd.assignment2.eventdriven.model.{RouteStation, TrainInfo}
import it.unibo.pcd.assignment2.eventdriven.view.LoadingLabel
import it.unibo.pcd.assignment2.eventdriven.view.cards.StopCard
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.{Button, ScrollPane, TextField}
import scalafx.geometry.Insets
import scalafx.scene.control.Label
import scalafx.scene.layout.VBox

sealed trait TrainTab extends Tab {
 def displayTrainInfo(trainInfo: TrainInfo): Unit
}

object TrainTab {
  import javafx.scene.control.Tab

  private final class TrainTabImpl(controller: Controller) extends TrainTab {
    private var updatedTrain: Option[String] = None
    @FXML
    private var trainCode: TextField = new TextField
    @FXML
    private var startMonitorTrain: Button = new Button
    @FXML
    private var stopMonitorTrain: Button = new Button
    @FXML
    private var stations: ScrollPane = new ScrollPane

    private val loader = new FXMLLoader
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource("trainTab.fxml"))
    override val tab: Tab = loader.load[Tab]
    stations.setPadding(Insets(0, 5.0, 0, 5.0))
    startMonitorTrain.setOnMouseClicked(_ => {
      startMonitorTrain.setDisable(true)
      stations.setContent(LoadingLabel().label)
      val station = trainCode.getText
      controller.startTrainInfoUpdates(station)
      updatedTrain = Some(station)
      stopMonitorTrain.setDisable(false)
    })
    stopMonitorTrain.setOnMouseClicked(_ => {
      stopMonitorTrain.setDisable(true)
      updatedTrain.foreach(controller.stopTrainInfoUpdates)
      updatedTrain = None
      startMonitorTrain.setDisable(false)
    })

    override def displayTrainInfo(trainInfo: TrainInfo): Unit = {
      val container = new VBox(5)
      container.padding = Insets(0, 0, 5.0, 0)
      setLabelForText(s"${trainInfo.train.trainType.toString} ${trainInfo.train.trainCode.getOrElse("")}", container)
      setLabelForText(getTextForState(trainInfo.stations), container)
      discard { container.children ++= trainInfo.stations.map(StopCard(_)).map(_.pane) }
      stations.setContent(container)
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
