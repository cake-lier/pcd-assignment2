package it.unibo.pcd.assignment2.eventdriven

import it.unibo.pcd.assignment2.eventdriven.model.{RouteArrivalStation, RouteDepartureStation, Train, TrainInfo, TrainType, TravelState}
import play.api.libs.json.{JsValue, Json}

import java.time.temporal.ChronoUnit
import java.time.LocalDateTime

object Mannaggia_Trenitalia extends App {

  import ImplicitConversion._
  def cheSchifoLAPI(trainStateJson: String): TrainInfo = {
    val json = Json.parse(trainStateJson);
    val stops = (json \ "fermate").as[List[JsValue]]
    // stations
    val arrivalStationJson = stops.last
    val departureStationJson = stops.head
    // departure platform
    val plannedDeparturePlatform = (departureStationJson \ "binarioProgrammatoPartenzaDescrizione").as[String]
    val actualDeparturePlatform = (departureStationJson \ "binarioEffettivoPartenzaDescrizione").asOpt[String]
    // departure dateTime
    val plannedDepartureDatetime = (departureStationJson \ "partenza_teorica").as[Long].toLocalDateTime
    val realDepartureDatetime = (departureStationJson \ "partenzaReale").asOpt[Long].map(_.toLocalDateTime)
    //delay
    val delayMinutes = (json \ "ritardo").as[Int]
    // arrival dateTime
    val isArrived = (arrivalStationJson \ "binarioEffettivoArrivoDescrizione").asOpt[String].nonEmpty

    val train:Train = Train((json \ "numeroTreno").asOpt[Int].map(_.toString),
      TrainTypeMap.map((json \ "compNumeroTreno").as[String].split(" ")(1)))
    val departureStation = RouteDepartureStation((departureStationJson \ "stazione").as[String],plannedDepartureDatetime,realDepartureDatetime,plannedDeparturePlatform,actualDeparturePlatform)
    TrainInfo(train,_computeTravelState(json,isArrived,delayMinutes),departureStation,_trainStops(stops.tail,delayMinutes))
  }

  def _computeTravelState(json:JsValue,isArrived:Boolean,delay:Int): TravelState = {
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

  def _trainStops(stops:List[JsValue],delayMinutes:Int):List[RouteArrivalStation] = stops map (_fromStopToArrivalStation(_,delayMinutes))

  def _fromStopToArrivalStation(stop:JsValue,delayMinutes:Int):RouteArrivalStation = {
    val stationName = (stop \"stazione").as[String]
    // datetime
    val plannedArrivalDatetime = (stop \ "arrivo_teorico").as[Long].toLocalDateTime
    val actualArrivalDatetime = plannedArrivalDatetime.plus(delayMinutes, ChronoUnit.MINUTES)
    // arrival platform
    val plannedArrivalPlatform = (stop \ "binarioProgrammatoArrivoDescrizione").as[String]
    val actualArrivalPlatform = (stop \ "binarioEffettivoArrivoDescrizione").asOpt[String]
    val realArrivalDatetime = (stop \ "arrivo_reale").asOpt[Long].map(_.toLocalDateTime)
    RouteArrivalStation(stationName,plannedArrivalDatetime,Some(actualArrivalDatetime),realArrivalDatetime,plannedArrivalPlatform,actualArrivalPlatform)
  }

  object TrainTypeMap{
    var map = Map("REG" -> TrainType.REGIONALE,
                      "FB"->TrainType.FRECCIABIANCA,
                      "FR"->TrainType.FRECCIAROSSA,
                      "FA"->TrainType.FRECCIARGENTO,
                      "IC"->TrainType.INTERCITY)
    //TODO INTERCITY e INTERCITY NOTTE e REG V????
  }

  object ImplicitConversion{
    implicit class TimeConverter(time:Long){
      def toLocalDateTime:LocalDateTime = TimeUtils.fromMillisToLocalDateTime(time)
    }
  }
}
