package it.unibo.pcd.assignment2.eventdriven.view.components.cards

/** Factory for creating cards for displaying the information contained in a
 *  [[it.unibo.pcd.assignment2.eventdriven.model.Stage]].
 */
object StageCard {
  import it.unibo.pcd.assignment2.eventdriven.model.Stage
  import javafx.fxml.FXML
  import javafx.scene.control.Label
  import javafx.scene.layout.{Pane, GridPane}
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component.AbstractComponent
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component

  import java.time.format.DateTimeFormatter

  /* Implementation of a card for Stages. */
  private class StageCardImpl(stage: Stage, index: Int) extends AbstractComponent[Pane](fxmlFileName =  "solutionStopCard.fxml") {
    @FXML
    private var stopTitle: Label = new Label
    @FXML
    private var stopArrival: Label = new Label
    @FXML
    private var stopDeparture: Label = new Label

    override val inner: Pane = loader.load[GridPane]
    stopTitle.setText(s"${index.toString}ª fermata: ${stage.stationName}")
    val dateTimePattern = "dd/MM/yyyy 'alle' HH:mm"
    val notAvailableMessage = "--"
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern)
    stopArrival.setText(s"${stopArrival.getText}${stage.arrivalDatetime.map(_.format(formatter)).getOrElse(notAvailableMessage)}")
    stopDeparture.setText(s"${stopDeparture.getText}${stage.departureDatetime
                                                           .map(_.format(formatter))
                                                           .getOrElse(notAvailableMessage)}")
  }

  /** Creates a new [[Component]] for displaying the information of a [[Stage]].
   *
   *  @param stage the [[Stage]] with the information to display
   *  @param index the position of the [[Stage]] with respect to the others in their
   *               [[it.unibo.pcd.assignment2.eventdriven.model.Transport]]
   *  @return a new instance of a [[Component]] for displaying a [[Stage]]
   */
  def apply(stage: Stage, index: Int): Component[Pane] = new StageCardImpl(stage, index)
}
