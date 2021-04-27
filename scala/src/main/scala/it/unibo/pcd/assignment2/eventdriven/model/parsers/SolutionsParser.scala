package it.unibo.pcd.assignment2.eventdriven.model.parsers

import it.unibo.pcd.assignment2.eventdriven.TimeUtils.fromMillisToLocalDateTime
import it.unibo.pcd.assignment2.eventdriven.model.{Solution, SolutionStation, SolutionTrain, Stop, TrainType}
import org.apache.commons.text.WordUtils

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId}

object SolutionsParser {
  import play.api.libs.json.{JsLookupResult, JsValue, Json}

  def apply(json: String, details: List[String]): List[Solution] = {
    val trainListKey = "trainlist"
    val priceKey = "minprice"
    val saleableKey = "saleable"
    val bookableKey = "bookable"
    val originKey = "origin"
    val destinationKey = "destination"
    val departureTimeKey = "departuretime"
    val arrivalTimeKey = "arrivaltime"
    Json.parse(json)
        .as[List[JsValue]]
        .zip(details)
        .map(s => Solution(
          parseSolutionTrains(s._1 \ trainListKey, Json.parse(s._2)),
          Some((s._1 \ priceKey).as[Double]).filter(_ > 0),
          (s._1 \ bookableKey).as[Boolean],
          (s._1 \ saleableKey).as[Boolean],
          SolutionStation((s._1 \ originKey).as[String], (s._1 \ departureTimeKey).as[Long]),
          SolutionStation((s._1 \ destinationKey).as[String], (s._1 \ arrivalTimeKey).as[Long])
        ))
  }

  private def parseSolutionTrains(json: JsLookupResult, detail: JsValue): List[SolutionTrain] = {
    val trainIdKey = "trainidentifier"
    val departureStationKey = "departurestation"
    val departureTimeKey = "departuretime"
    val arrivalStationKey = "arrivalstation"
    val arrivalTimeKey = "arrivaltime"
    val stopsListKey = "stoplist"
    val stationNameKey = "stationname"
    val trainCodePattern = ".* ([0-9]+)$".r
    val trainTypePattern = "^([a-zA-Z]+ [a-zA-Z]+|[a-zA-Z]+) .*".r
    json.as[List[JsValue]]
        .zip(detail.as[List[JsValue]])
        .map(t => SolutionTrain(
          (t._1 \ trainIdKey).as[String] match {
            case trainCodePattern(trainCode) => Some(trainCode)
            case _ => None
          },
          (t._1 \ trainIdKey).as[String] match {
            case trainTypePattern(trainType) =>
              TrainType.values.toList.find(_.toString == trainType.replace(" ", "_").toUpperCase).getOrElse(TrainType.AUTOBUS)
            case _ => TrainType.AUTOBUS
          },
          parseSolutionStation(t._2, departureStationKey, departureTimeKey),
          parseSolutionStation(t._2, arrivalStationKey, arrivalTimeKey),
          (t._2 \ stopsListKey).as[List[JsValue]]
                             .map(o => Stop(WordUtils.capitalizeFully((o \ stationNameKey).as[String]),
                                            parseSolutionDatetime(o, departureTimeKey),
                                            parseSolutionDatetime(o, arrivalTimeKey)))
        ))
  }

  private def parseSolutionStation(detail: JsValue, stationKey: String, datetimeKey: String): SolutionStation =
    SolutionStation(WordUtils.capitalizeFully((detail \ stationKey).as[String]),
                    LocalDateTime.ofInstant(Instant.ofEpochMilli((detail \ datetimeKey).as[Long]), ZoneId.systemDefault()))

  private def parseSolutionDatetime(value: JsValue, datetimeKey: String): Option[LocalDateTime] =
    (value \ datetimeKey).asOpt[String].map(i => LocalDateTime.parse(i, DateTimeFormatter.ISO_DATE_TIME))
}