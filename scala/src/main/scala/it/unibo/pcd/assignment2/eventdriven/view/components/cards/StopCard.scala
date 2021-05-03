package it.unibo.pcd.assignment2.eventdriven.view.components.cards

/** A card to display the information contained in a [[it.unibo.pcd.assignment2.eventdriven.model.Stop]]. */
object StopCard {
  import it.unibo.pcd.assignment2.eventdriven.AnyOps.AnyOps
  import it.unibo.pcd.assignment2.eventdriven.model.{Stop, TravelState}
  import it.unibo.pcd.assignment2.eventdriven.model.TravelState.{Delayed, Early, InTime, Nothing}
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component.AbstractComponent
  import javafx.fxml.FXML
  import javafx.scene.control.Label
  import javafx.scene.layout.{GridPane, Pane}

  import java.time.format.DateTimeFormatter

  /* Implementation of a card for displaying a Stop. */
  private class StopCardImpl(stop: Stop) extends AbstractComponent[Pane](fxmlFileName = "stopCard.fxml") {
    @FXML
    private var stopTitle: Label = new Label
    @FXML
    private var departureInfo: Label = new Label
    @FXML
    private var arrivalInfo: Label = new Label
    @FXML
    private var platformInfo: Label = new Label
    @FXML
    private var stateInfo: Label = new Label

    override val inner: Pane = loader.load[GridPane]
    stopTitle.setText(stop.stationName)
    val datetimePattern = "dd/MM/yyyy 'alle' HH:mm"
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(datetimePattern)
    val notAvailableMessage = "--"
    departureInfo.setText(
      stop.plannedDepartureDatetime
          .map(p => s"Partenza programmata: ${p.format(formatter)}    " +
                    s"${stop.actualDepartureDatetime.map(a => s"Partenza effettiva: ${a.format(formatter)} ").getOrElse("")}")
          .getOrElse(notAvailableMessage)
    )
    arrivalInfo.setText(
      stop.plannedArrivalDatetime
          .map(p => s"Arrivo programmato: ${p.format(formatter)}    " +
                    s"${stop.actualArrivalDatetime.map(a => s"Arrivo effettivo: ${a.format(formatter)}").getOrElse("")}")
          .getOrElse(notAvailableMessage)
    )
    platformInfo.setText(s"Binario programmato: ${stop.plannedPlatform.getOrElse(notAvailableMessage)}    " +
                         s"Binario effettivo: ${stop.actualPlatform.getOrElse(notAvailableMessage)}")
    stateInfo.setText((stop.arrivalState, stop.departureState) match {
      case (a, d) if a =/= Nothing && d =/= Nothing => s"Il treno è arrivato ${getTextFromState(a)} ed è partito " +
                                                       s"${getTextFromState(d)}"
      case (a, _) if a =/= Nothing => s"Il treno è arrivato ${getTextFromState(a)} e non è partito"
      case (a, d) if a === Nothing && d =/= Nothing => s"Il treno è partito ${getTextFromState(d)}"
      case _ => notAvailableMessage
    })

    /* Returns the text to display for the given TravelState. */
    private def getTextFromState(travelState: TravelState): String = travelState match {
      case InTime => "in orario"
      case Delayed(m) => s"con ${m.toString} minuti di ritardo"
      case Early(m) => s"con ${m.toString} minuti di anticipo"
      case _ => ""
    }
  }

  /** Creates a new instance of a [[Component]] for displaying a [[Stop]].
   *
   *  @param stop the [[Stop]] which information is to display
   *  @return a new instance of a [[Component]] for displaying a [[Stop]]
   */
  def apply(stop: Stop): Component[Pane] = new StopCardImpl(stop)
}

