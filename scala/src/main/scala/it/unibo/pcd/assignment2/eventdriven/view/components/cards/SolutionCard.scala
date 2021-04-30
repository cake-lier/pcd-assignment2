package it.unibo.pcd.assignment2.eventdriven.view.components.cards

object SolutionCard {
  import it.unibo.pcd.assignment2.eventdriven.model.Solution
  import it.unibo.pcd.assignment2.eventdriven.AnyOps.discard
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component.AbstractComponent
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component
  import javafx.fxml.FXML
  import javafx.scene.control.{Accordion, Label}
  import javafx.scene.layout.GridPane

  import java.time.format.DateTimeFormatter

  private class SolutionCardImpl(solution: Solution) extends AbstractComponent[GridPane]("solutionCard.fxml") {
    @FXML
    private var trainsField: Accordion = new Accordion
    @FXML
    private var priceField: Label = new Label
    @FXML
    private var bookableField: Label = new Label
    @FXML
    private var departureField: Label = new Label
    @FXML
    private var arrivalField: Label = new Label

    override val inner: GridPane = loader.load[GridPane]
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
    discard { trainsField.getPanes.setAll(solution.trains.map(TrainCard(_).inner): _*) }
  }

  def apply(solution: Solution): Component[GridPane] = new SolutionCardImpl(solution)
}
