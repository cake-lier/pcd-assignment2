package it.unibo.pcd.assignment2.eventdriven.model.parsers

import java.time.LocalDateTime

/** A parser for creating [[it.unibo.pcd.assignment2.eventdriven.model.TrainInfo]] instances. */
object TrainInfoParser {
  import it.unibo.pcd.assignment2.eventdriven.AnyOps.AnyOps
  import it.unibo.pcd.assignment2.eventdriven.TimeHelpers
  import it.unibo.pcd.assignment2.eventdriven.model.TravelState.{Delayed, Early, InTime, Nothing}
  import it.unibo.pcd.assignment2.eventdriven.model._
  import org.apache.commons.text.WordUtils
  import play.api.libs.json.{JsValue, Json}
  import TimeHelpers.millisToLocalDateTime

  /* Creates the correct TravelState object from the given information. */
  private def calculateTravelState(datetime: Option[LocalDateTime], delay: Int): TravelState = (datetime, delay) match {
    case (Some(_), d) if d > 0 => Delayed(d)
    case (Some(_), d) if d < 0 => Early(-d)
    case (Some(_), _) => InTime
    case _ => Nothing
  }

  /* Extracts a datetime field from a JSON. */
  private def extractDatetime(json: JsValue, key: String): Option[LocalDateTime] =
    (json \ key).asOpt[Long].map(millisToLocalDateTime)

  /** Parses a JSON-formatted [[String]] into a [[TrainInfo]].
   *
   *  This method is built upon the Trenitalia JSON responses, so the format of the inputted JSONs should respect the format of
   *  those JSON responses.
   *  @param json the JSON-formatted [[String]] containing the list of [[TrainInfo]]
   *  @return a [[TrainInfo]] as parsed from the input
   */
  def apply(json: String): TrainInfo = {
    val parsed = Json.parse(json)
    val trainCodeKey = "numeroTreno"
    val trainTypeKey = "categoria"
    val stopsKey = "fermate"
    TrainInfo(
      Train((parsed \ trainCodeKey).asOpt[Int].map(_.toString),
            TrainType.values.find(_.code === (parsed \ trainTypeKey).as[String]).getOrElse(TrainType.Autobus)),
      (parsed \ stopsKey).as[List[JsValue]]
                         .map(o => {
                           val actualDeparture = extractDatetime(o, key = "partenzaReale")
                           val actualArrival = extractDatetime(o, key = "arrivoReale")
                           val stationNameKey = "stazione"
                           val plannedPlatformNameKey = "binarioProgrammatoPartenzaDescrizione"
                           val altPlannedPlatformNameKey = "binarioProgrammatoArrivoDescrizione"
                           val actualPlatformNameKey = "binarioEffettivoPartenzaDescrizione"
                           val altActualPlatformNameKey = "binarioEffettivoArrivoDescrizione"
                           val delayDepartureKey = "ritardoPartenza"
                           val delayArrivalKey = "ritardoArrivo"
                           Stop(
                             WordUtils.capitalizeFully((o \ stationNameKey).as[String]),
                             extractDatetime(o, key = "partenza_teorica"),
                             actualDeparture,
                             extractDatetime(o, key = "arrivo_teorico"),
                             actualArrival,
                             (o \ plannedPlatformNameKey).asOpt[String].orElse((o \ altPlannedPlatformNameKey).asOpt[String]),
                             (o \ actualPlatformNameKey).asOpt[String].orElse((o \ altActualPlatformNameKey).asOpt[String]),
                             calculateTravelState(actualDeparture, (o \ delayDepartureKey).as[Int]),
                             calculateTravelState(actualArrival, (o \ delayArrivalKey).as[Int])
                           )
                         })
    )
  }
}
