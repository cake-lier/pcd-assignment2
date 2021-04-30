package it.unibo.pcd.assignment2.eventdriven.view.components.cards

import javafx.scene.layout.GridPane

object SolutionStopCard {
  import it.unibo.pcd.assignment2.eventdriven.model.Stop
  import javafx.fxml.FXML
  import javafx.scene.control.Label
  import javafx.scene.layout.Pane
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component.AbstractComponent
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component

  import java.time.format.DateTimeFormatter

  private class SolutionStopCardImpl(stop: Stop, index: Int) extends AbstractComponent[Pane]("solutionStopCard.fxml") {
    @FXML
    private var stopTitle: Label = new Label
    @FXML
    private var stopArrival: Label = new Label
    @FXML
    private var stopDeparture: Label = new Label

    override val inner: Pane = loader.load[GridPane]
    stopTitle.setText(s"${index.toString}ª fermata: ${stop.stationName}")
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle' HH:mm")
    stopArrival.setText(s"${stopArrival.getText}${stop.arrivalDatetime.map(_.format(formatter)).getOrElse("--")}")
    stopDeparture.setText(s"${stopDeparture.getText}${stop.departureDatetime.map(_.format(formatter)).getOrElse("--")}")
  }

  def apply(stop: Stop, index: Int): Component[Pane] = new SolutionStopCardImpl(stop, index)
}
