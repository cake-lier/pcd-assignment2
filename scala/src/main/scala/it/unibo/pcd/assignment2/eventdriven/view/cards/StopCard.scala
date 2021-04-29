package it.unibo.pcd.assignment2.eventdriven.view.cards

import it.unibo.pcd.assignment2.eventdriven.AnyOps.AnyOps
import it.unibo.pcd.assignment2.eventdriven.model.{RouteStation, TravelState}
import it.unibo.pcd.assignment2.eventdriven.model.TravelState.{Delayed, Early, InTime, Nothing}
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.Label
import javafx.scene.layout.{GridPane, Pane}

import java.time.format.DateTimeFormatter

object StopCard {
  private class StopCardImpl(station: RouteStation) extends Card[Pane] {
    @FXML
    private var stopTitle: Label = new Label()
    @FXML
    private var departureInfo: Label = new Label()
    @FXML
    private var arrivalInfo: Label = new Label()
    @FXML
    private var platformInfo: Label = new Label()
    @FXML
    private var stateInfo: Label = new Label()

    private val loader = new FXMLLoader
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource("stopCard.fxml"))
    override val pane: Pane = loader.load[GridPane]
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

  def apply(station: RouteStation): Card[Pane] = new StopCardImpl(station)
}

