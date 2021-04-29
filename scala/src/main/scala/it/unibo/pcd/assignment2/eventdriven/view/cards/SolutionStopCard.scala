package it.unibo.pcd.assignment2.eventdriven.view.cards

import it.unibo.pcd.assignment2.eventdriven.model.Stop
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.Label
import javafx.scene.layout.{GridPane, Pane}

import java.time.format.DateTimeFormatter

object SolutionStopCard {
  private class SolutionStopCardImpl(stop: Stop, index: Int) extends Card[Pane] {
    @FXML
    private var stopTitle: Label = new Label()
    @FXML
    private var stopArrival: Label = new Label()
    @FXML
    private var stopDeparture: Label = new Label()

    val loader = new FXMLLoader
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource("solutionStopCard.fxml"))
    override val pane: Pane = loader.load[GridPane]
    stopTitle.setText(s"${index.toString}ª fermata: ${stop.stationName}")
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle' HH:mm")
    stopArrival.setText(s"${stopArrival.getText}${stop.arrivalDatetime.map(_.format(formatter)).getOrElse("--")}")
    stopDeparture.setText(s"${stopDeparture.getText}${stop.departureDatetime.map(_.format(formatter)).getOrElse("--")}")
  }

  def apply(stop: Stop, index: Int): Card[Pane] = new SolutionStopCardImpl(stop, index)
}
