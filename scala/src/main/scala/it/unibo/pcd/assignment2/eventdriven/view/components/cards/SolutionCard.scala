package it.unibo.pcd.assignment2.eventdriven.view.components.cards

/** Factory for creating cards containing information about a [[it.unibo.pcd.assignment2.eventdriven.model.Solution]]. */
object SolutionCard {
  import it.unibo.pcd.assignment2.eventdriven.model.Solution
  import it.unibo.pcd.assignment2.eventdriven.AnyOps.discard
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component.AbstractComponent
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component
  import javafx.fxml.FXML
  import javafx.scene.control.{Accordion, Label}
  import javafx.scene.layout.GridPane

  import java.time.format.DateTimeFormatter

  /* Implementation of a card for Solutions. */
  private class SolutionCardImpl(solution: Solution) extends AbstractComponent[GridPane](fxmlFileName = "solutionCard.fxml") {
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

    val notSaleableMessage = "La soluzione non è acquistabile"
    priceField.setText(solution.price
                               .filter(_ => solution.saleable)
                               .map(d => f"Costo: $d%.2f €")
                               .getOrElse(notSaleableMessage))
    bookableField.setText(s"La soluzione ${ if (solution.bookable) "" else "non " }è prenotabile")
    val datetimePattern = "dd/MM/YY 'alle' HH:mm"
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(datetimePattern)
    departureField.setText(s"${departureField.getText}${solution.departureStation.stationName} " +
                           s"il ${solution.departureStation.datetime.format(formatter)}")
    arrivalField.setText(s"${arrivalField.getText}${solution.arrivalStation.stationName} " +
                         s"il ${solution.arrivalStation.datetime.format(formatter)}")
    discard { trainsField.getPanes.setAll(solution.transports.map(TransportCard(_).inner): _*) }
  }

  /** Creates a new instance of a [[Component]] for displaying the information of a [[Solution]].
   *
   *  @param solution the [[Solution]] of which displaying the information
   *  @return a [[Component]] for displaying a [[Solution]]
   */
  def apply(solution: Solution): Component[GridPane] = new SolutionCardImpl(solution)
}
