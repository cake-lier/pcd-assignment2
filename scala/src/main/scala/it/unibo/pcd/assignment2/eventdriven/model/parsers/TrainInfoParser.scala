package it.unibo.pcd.assignment2.eventdriven.model.parsers

import it.unibo.pcd.assignment2.eventdriven.TimeUtils
import it.unibo.pcd.assignment2.eventdriven.model._
import org.apache.commons.text.WordUtils
import play.api.libs.json.{JsValue, Json}

import java.time.temporal.ChronoUnit

sealed trait TrainInfoParser extends Parser[TrainInfo]

object TrainInfoParser extends TrainInfoParser {
  import TimeUtils._

  override def parse(trainStateJson: String): TrainInfo = {
    val json = Json.parse(trainStateJson)
    val stops = (json \ "fermate").as[List[JsValue]]
    val departureStationJson = stops.head
    val delayMinutes = (json \ "ritardo").as[Int]
    TrainInfo(
      Train(
        (json \ "numeroTreno").asOpt[Int].map(_.toString),
        TrainTypeMap.map((json \ "compNumeroTreno").as[String].split(" ")(0))
      ),
      _computeTravelState(
        json,
        (stops.last \ "binarioEffettivoArrivoDescrizione").asOpt[String].nonEmpty,
        delayMinutes
      ),
      RouteDepartureStation(
        WordUtils.capitalizeFully((departureStationJson \ "stazione").as[String]),
        (departureStationJson \ "partenza_teorica").as[Long],
        (departureStationJson \ "partenzaReale").asOpt[Long].map(fromMillisToLocalDateTime),
        (departureStationJson \ "binarioProgrammatoPartenzaDescrizione").as[String],
        (departureStationJson \ "binarioEffettivoPartenzaDescrizione").asOpt[String]
      ),
      stops.map(_fromStopToArrivalStation(_, delayMinutes))
    )
  }

  def _computeTravelState(json: JsValue, isArrived: Boolean, delay: Int): TravelState = {
    val isDeparted = !(json \ "nonPartito").as[Boolean]
    if (!isDeparted) {
      return TravelState.NotDeparted
    }
    if (isDeparted && isArrived) {
      return TravelState.Arrived
    }
    if (isDeparted && delay > 0) {
      return TravelState.Delayed(delay)
    }
    if (isDeparted && delay < 0) {
      return TravelState.Early(delay)
    }
    TravelState.InTime
  }

  def _fromStopToArrivalStation(stop: JsValue, delayMinutes: Int): RouteArrivalStation = {
    val plannedArrivalDatetime = (stop \ "arrivo_teorico").as[Long]
    RouteArrivalStation(
      WordUtils.capitalizeFully((stop \ "stazione").as[String]),
      plannedArrivalDatetime,
      Some(plannedArrivalDatetime.plus(delayMinutes, ChronoUnit.MINUTES)),
      (stop \ "arrivo_reale").asOpt[Long].map(fromMillisToLocalDateTime),
      (stop \ "binarioProgrammatoArrivoDescrizione").as[String],
      (stop \ "binarioEffettivoArrivoDescrizione").asOpt[String]
    )
  }

  object TrainTypeMap {
    var map = Map(
      "REG" -> TrainType.REGIONALE,
      "FB" -> TrainType.FRECCIABIANCA,
      "FR" -> TrainType.FRECCIAROSSA,
      "FA" -> TrainType.FRECCIARGENTO,
      "IC" -> TrainType.INTERCITY,
      "RV" -> TrainType.REGIONALE_VELOCE,
      "EC" -> TrainType.EUROCITY,
      "ICN" -> TrainType.INTERCITY_NOTTE)
  }
}
