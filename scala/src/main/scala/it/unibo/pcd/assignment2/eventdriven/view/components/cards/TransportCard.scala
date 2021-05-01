package it.unibo.pcd.assignment2.eventdriven.view.components.cards

/** Factory for creating new instances of cards for displaying the information contained in a
 *  [[it.unibo.pcd.assignment2.eventdriven.model.Transport]].
 */
object TransportCard {
  import it.unibo.pcd.assignment2.eventdriven.model.Transport
  import it.unibo.pcd.assignment2.eventdriven.AnyOps.discard
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component.AbstractComponent
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component
  import javafx.fxml.FXML
  import javafx.scene.control.{Label, TitledPane}
  import javafx.scene.layout.VBox

  import java.time.format.DateTimeFormatter

  /* Implementation of a card for displaying a Transport. */
  private class TransportCardImpl(transport: Transport) extends AbstractComponent[TitledPane]("trainCard.fxml") {
    @FXML
    private var departureField: Label = new Label
    @FXML
    private var arrivalField: Label = new Label
    @FXML
    private var stopsField: VBox = new VBox

    override val inner: TitledPane = loader.load[TitledPane]
    inner.setText(s"${transport.trainType.toString} ${transport.trainCode.getOrElse("")}")
    val datetimePattern = "dd/MM/yyyy 'alle' HH:mm"
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(datetimePattern)
    departureField.setText(s"${departureField.getText}${transport.departureStation.stationName} il " +
                           s"${transport.departureStation.datetime.format(formatter)}")
    arrivalField.setText(s"${arrivalField.getText}${transport.arrivalStation.stationName} il " +
                         s"${transport.arrivalStation.datetime.format(formatter)}")
    discard { stopsField.getChildren.setAll(transport.stages.zipWithIndex.map(p => StageCard(p._1, p._2 + 1).inner): _*) }
  }

  /** Creates a new instance of a [[Component]] for displaying a [[Transport]].
   *
   *  @param transport the [[Transport]] of which displaying the information
   *  @return a new instance of a [[Component]] for displaying a [[Transport]]
   */
  def apply(transport: Transport): Component[TitledPane] = new TransportCardImpl(transport)
}