package it.unibo.pcd.assignment2.eventdriven.model.parsers

/** A parser for creating [[it.unibo.pcd.assignment2.eventdriven.model.Solution]] instances. */
object SolutionsParser {
  import play.api.libs.json.{JsLookupResult, JsValue, Json}
  import it.unibo.pcd.assignment2.eventdriven.AnyOps.AnyOps
  import it.unibo.pcd.assignment2.eventdriven.TimeHelpers.millisToLocalDateTime
  import it.unibo.pcd.assignment2.eventdriven.model.{TimestampedStation, Transport, Stage, TrainType, Solution}
  import org.apache.commons.text.WordUtils

  import java.time.format.DateTimeFormatter
  import java.time.{Instant, LocalDateTime, ZoneId}
  import java.util.Locale

  /* Parses the part of the JSON containing the SolutionTrains. */
  private def parseSolutionTrains(json: JsLookupResult, detail: JsValue): List[Transport] = {
    val trainIdKey = "trainidentifier"
    val departureStationKey = "departurestation"
    val departureTimeKey = "departuretime"
    val arrivalStationKey = "arrivalstation"
    val arrivalTimeKey = "arrivaltime"
    val stopsListKey = "stoplist"
    val stationNameKey = "stationname"
    val trainCodePattern = ".* ([0-9A-Za-z]+)$".r
    val trainTypePattern = "^([a-zA-Z]+ [a-zA-Z]+|[a-zA-Z]+) .*".r
    json.as[List[JsValue]]
        .zip(detail.as[List[JsValue]])
        .map(t => Transport(
          (t._1 \ trainIdKey).as[String] match {
            case trainCodePattern(trainCode) => Some(trainCode)
            case _ => None
          },
          (t._1 \ trainIdKey).as[String] match {
            case trainTypePattern(trainType) =>
              TrainType.values
                       .find(_.toString.toUpperCase(Locale.ITALY) === trainType.toUpperCase(Locale.ITALY))
                       .getOrElse(TrainType.Autobus)
            case _ => TrainType.Autobus
          },
          parseSolutionStation(t._2, departureStationKey, departureTimeKey),
          parseSolutionStation(t._2, arrivalStationKey, arrivalTimeKey),
          (t._2 \ stopsListKey).asOpt[List[JsValue]]
                               .getOrElse(List[JsValue]())
                               .map(o => Stage(WordUtils.capitalizeFully((o \ stationNameKey).as[String]),
                                              parseSolutionDatetime(o, departureTimeKey),
                                              parseSolutionDatetime(o, arrivalTimeKey)))
        ))
  }

  /* Parses the part of the JSON containing the SolutionStation. */
  private def parseSolutionStation(detail: JsValue, stationKey: String, datetimeKey: String): TimestampedStation =
    TimestampedStation(WordUtils.capitalizeFully((detail \ stationKey).as[String]),
                    LocalDateTime.ofInstant(Instant.ofEpochMilli((detail \ datetimeKey).as[Long]), ZoneId.systemDefault()))

  /* Parses a datetime field from the JSON. */
  private def parseSolutionDatetime(value: JsValue, datetimeKey: String): Option[LocalDateTime] =
    (value \ datetimeKey).asOpt[String].map(i => LocalDateTime.parse(i, DateTimeFormatter.ISO_DATE_TIME))

  /** Parses a JSON-formatted [[String]] into a list of [[Solution]]s given the detail information for each solution as a
   *  JSON-formatted string.
   *
   *  This method is built upon the Trenitalia JSON responses, so the format of the inputted JSONs should respect the format of
   *  those JSON responses. If more [[Solution]]s than details are parsed, or vice-versa, the excess elements are ignored. The
   *  pairing is made index-wise.
   *  @param json the JSON-formatted [[String]] containing the list of [[Solution]]s
   *  @param details the [[List]] of detail information as JSON-formatted [[String]]s, one for each solution
   *  @return a [[List]] of [[Solution]] as parsed from the inputs
   */
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
          TimestampedStation((s._1 \ originKey).as[String], (s._1 \ departureTimeKey).as[Long]),
          TimestampedStation((s._1 \ destinationKey).as[String], (s._1 \ arrivalTimeKey).as[Long])
        ))
  }
}