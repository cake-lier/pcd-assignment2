package it.unibo.pcd.assignment2.eventdriven.view.tabs

import it.unibo.pcd.assignment2.eventdriven.controller.Controller
import it.unibo.pcd.assignment2.eventdriven.model.TravelState._
import it.unibo.pcd.assignment2.eventdriven.model.{TrainInfo, TravelState}
import it.unibo.pcd.assignment2.eventdriven.view.LoadingLabel
import it.unibo.pcd.assignment2.eventdriven.view.cards.{HalfwayStopCard, InitialStopCard}
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.{Button, ScrollPane, TextField}
import org.apache.commons.text.WordUtils
import scalafx.geometry.Insets
import scalafx.scene.control.Label
import scalafx.scene.layout.VBox

sealed trait TrainTab extends Tab {
 def displayTrainInfo(trainInfo: TrainInfo): Unit
}

object TrainTab {
  import javafx.scene.control.Tab

  private class TrainTabImpl(controller: Controller) extends TrainTab {
    private var updatedTrain: Option[String] = None
    @FXML
    private var root: Tab = _
    @FXML
    private var trainCode: TextField = _
    @FXML
    private var startMonitorTrain: Button = _
    @FXML
    private var stopMonitorTrain: Button = _
    @FXML
    private var stations: ScrollPane = _

    private val loader = new FXMLLoader
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource("trainTab.fxml"))
    loader.load
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

    override val tab: Tab = root

    override def displayTrainInfo(trainInfo: TrainInfo): Unit = {
      val container = new VBox(5)
      setLabelForText(
        s"${WordUtils.capitalizeFully(trainInfo.train.trainType.toString.replace("_", " "))} " +
        s"${trainInfo.train.trainCode.getOrElse("")}",
        container
      )
      setLabelForText(getTextForState(trainInfo.state), container)
      container.children += InitialStopCard(trainInfo.departureStation).pane
      container.children.addAll(trainInfo.arrivalStations.map(HalfwayStopCard(_)).map(_.pane))
      stations.setContent(container)
    }

    private def setLabelForText(text: String, container: VBox): Unit = {
      val label = new Label(text)
      label.maxWidth = Double.MaxValue
      label.maxHeight = Double.MaxValue
      label.margin = Insets(5.0, 0, 0, 0)
      label.styleClass = List("gridCell")
      label.applyCss()
      container.children += label
    }

    private def getTextForState(travelState: TravelState): String = travelState match {
      case NotDeparted => "Il treno non è ancora partito"
      case Arrived => "Il treno è già arrivato"
      case InTime => "Il treno è in orario"
      case t: Delayed => s"Il treno è in ritardo di ${t.delay.get} minuti"
      case t: Early => s"Il treno è in anticipo di ${-t.delay.get} minuti"
    }
  }

  def apply(controller: Controller): TrainTab = new TrainTabImpl(controller)
}
