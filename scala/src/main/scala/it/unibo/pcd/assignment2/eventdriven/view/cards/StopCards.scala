package it.unibo.pcd.assignment2.eventdriven.view.cards

import it.unibo.pcd.assignment2.eventdriven.model.{RouteArrivalStation, RouteDepartureStation, RouteStation}
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.Label
import javafx.scene.layout.{GridPane, Pane}

import java.time.format.DateTimeFormatter

private abstract class StopCard(station: RouteStation) extends Card[Pane] {
    @FXML
    private var root: GridPane = _
    @FXML
    private var stopTitle: Label = _
    @FXML
    private var plannedInfo: Label = _
    @FXML
    protected var actualInfo: Label = _

    private val loader = new FXMLLoader
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource("stopCard.fxml"))
    loader.load
    stopTitle.setText(station.stationName)
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle' HH:mm")
    plannedInfo.setText(
        s"Arrivo programmato il ${station.plannedDatetime.format(formatter)} al binario ${station.plannedPlatform}"
    )

    override val pane: Pane = root
}

object HalfwayStopCard {
  private class HalfwayStopCard(station: RouteArrivalStation) extends StopCard(station) {
    actualInfo.setText(station.actualDatetime
                              .map(t => s"Arrivato il ${t.format(formatter)} " +
                                        s"${station.actualPlatform.map(p => s"al binario $p").getOrElse("")}")
                              .getOrElse(
                                  station.estimatedDatetime.map(t => s"Arrivo atteso il ${t.format(formatter)}").getOrElse("--")
                              ))
  }

  def apply(station: RouteArrivalStation): Card[Pane] = new HalfwayStopCard(station)
}

object InitialStopCard {
  private class InitialStopCard(station: RouteDepartureStation) extends StopCard(station) {
    actualInfo.setText(
      station.actualDatetime
             .map(t => s"Arrivato il ${t.format(formatter)} ${station.actualPlatform.map(p => s"al binario $p").getOrElse("")}")
             .getOrElse("--")
    )
  }

  def apply(station: RouteDepartureStation): Card[Pane] = new InitialStopCard(station)
}
