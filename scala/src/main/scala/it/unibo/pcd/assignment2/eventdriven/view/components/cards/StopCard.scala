package it.unibo.pcd.assignment2.eventdriven.view.components.cards

import javafx.scene.layout.GridPane

object StopCard {
  import it.unibo.pcd.assignment2.eventdriven.AnyOps.AnyOps
  import it.unibo.pcd.assignment2.eventdriven.model.{RouteStation, TravelState}
  import it.unibo.pcd.assignment2.eventdriven.model.TravelState.{Delayed, Early, InTime, Nothing}
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component.AbstractComponent
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component

  import java.time.format.DateTimeFormatter
  import javafx.fxml.FXML
  import javafx.scene.control.Label
  import javafx.scene.layout.Pane

  private class StopCardImpl(station: RouteStation) extends AbstractComponent[Pane]("stopCard.fxml") {
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
    stopTitle.setText(station.stationName)
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle' HH:mm")
    departureInfo.setText(
      station.plannedDepartureDatetime
             .map(p => s"Partenza programmata: ${p.format(formatter)}    " +
                       s"${station.actualDepartureDatetime
                                  .map(a => s"Partenza effettiva: ${a.format(formatter)} ")
                                  .getOrElse("")}")
             .getOrElse("--")
    )
    arrivalInfo.setText(
      station.plannedArrivalDatetime
             .map(p => s"Arrivo programmato: ${p.format(formatter)}    " +
                       s"${station.actualArrivalDatetime.map(a => s"Arrivo effettivo: ${a.format(formatter)}")
                                                        .getOrElse("")}")
             .getOrElse("--")
    )
    platformInfo.setText(s"Binario programmato: ${station.plannedPlatform.getOrElse("--")}    " +
                         s"Binario effettivo: ${station.actualPlatform.getOrElse("--")}")
    stateInfo.setText((station.arrivalState, station.departureState) match {
      case (a, d) if a =/= Nothing && d =/= Nothing => s"Il treno è arrivato ${getTextFromState(a)} ed è partito " +
                                                     s"${getTextFromState(d)}"
      case (a, _) if a =/= Nothing => s"Il treno è arrivato ${getTextFromState(a)} e non è partito"
      case (a, d) if a === Nothing && d =/= Nothing => s"Il treno è partito ${getTextFromState(d)}"
      case _ => "--"
    })

    private def getTextFromState(travelState: TravelState): String = travelState match {
      case InTime => "in orario"
      case Delayed(m) => s"con ${m.toString} minuti di ritardo"
      case Early(m) => s"con ${m.toString} minuti di anticipo"
      case _ => ""
    }
  }

  def apply(station: RouteStation): Component[Pane] = new StopCardImpl(station)
}

