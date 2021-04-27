package it.unibo.pcd.assignment2.eventdriven.view.cards

import it.unibo.pcd.assignment2.eventdriven.model.Solution
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.{Accordion, Label}
import javafx.scene.layout.GridPane

import java.time.format.DateTimeFormatter

object SolutionCard {
  private class SolutionCardImpl(solution: Solution) extends Card[GridPane] {
    @FXML
    private var root: GridPane = _
    @FXML
    private var trainsField: Accordion = _
    @FXML
    private var priceField: Label = _
    @FXML
    private var bookableField: Label = _
    @FXML
    private var departureField: Label = _
    @FXML
    private var arrivalField: Label = _

    val loader = new FXMLLoader
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource("solutionCard.fxml"))
    loader.load()
    priceField.setText(solution.price
                               .filter(_ => solution.saleable)
                               .map(d => f"Costo: $d%.2f €")
                               .getOrElse("La soluzione non è acquistabile"))
    bookableField.setText(s"La soluzione ${ if (solution.bookable) "" else "non " }è prenotabile")
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/YY 'alle' HH:mm")
    departureField.setText(s"${departureField.getText}${solution.departureStation.stationName} " +
                           s"il ${solution.departureStation.datetime.format(dateTimeFormatter)}")
    arrivalField.setText(s"${arrivalField.getText}${solution.arrivalStation.stationName} " +
                         s"il ${solution.arrivalStation.datetime.format(dateTimeFormatter)}")
    trainsField.getPanes.setAll(solution.trains.map(TrainCard(_)).map(_.pane): _*)

    override val pane: GridPane = root
  }

  def apply(solution: Solution): Card[GridPane] = new SolutionCardImpl(solution)
}
