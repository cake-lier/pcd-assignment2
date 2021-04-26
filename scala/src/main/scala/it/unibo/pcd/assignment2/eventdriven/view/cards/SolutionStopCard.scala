package it.unibo.pcd.assignment2.eventdriven.view.cards

import it.unibo.pcd.assignment2.eventdriven.model.Stop
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.Label
import javafx.scene.layout.{GridPane, Pane}

import java.time.format.DateTimeFormatter

object SolutionStopCard {
  private class SolutionStopCardImpl(stop: Stop, index: Int) extends Card[Pane] {
    @FXML
    private var root: GridPane = _
    @FXML
    private var stopTitle: Label = _
    @FXML
    private var stopArrival: Label = _
    @FXML
    private var stopDeparture: Label = _

    val loader = new FXMLLoader
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource("solutionStopCard.fxml"))
    loader.load()
    stopTitle.setText(s"${index}ª fermata: ${stop.stationName}")
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle' HH:mm")
    stopArrival.setText(s"${stopArrival.getText}${stop.arrivalDatetime.map(_.format(formatter)).getOrElse("--")}")
    stopDeparture.setText(s"${stopDeparture.getText}${stop.departureDatetime.map(_.format(formatter)).getOrElse("--")}")

    override def pane: Pane = root
  }

  def apply(stop: Stop, index: Int): Card[Pane] = new SolutionStopCardImpl(stop, index)
}
