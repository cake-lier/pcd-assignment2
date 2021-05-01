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

  /* Extracts a TimestampedStation from a JSON. */
  private def extractTimestampedStation(json: JsValue, nameKey: String, timestampKey: String): TimestampedStation =
    TimestampedStation(WordUtils.capitalizeFully((json \ nameKey).as[String]), (json \ timestampKey).as[Long])

  /** Parses a JSON-formatted [[String]] into a [[TrainInfo]].
   *
   *  This method is built upon the Trenitalia JSON responses, so the format of the inputted JSONs should respect the format of
   *  those JSON responses.
   *  @param json the JSON-formatted [[String]] containing the list of [[TrainInfo]]
   *  @return a [[TrainInfo]] as parsed from the input
   */
  def apply(json: String): TrainInfo = {
    val parsed = Json.parse(json)
    TrainInfo(
      Route(
        (parsed \ "numeroTreno").asOpt[Int].map(_.toString),
        TrainType.values.find(_.code === (parsed \ "categoria").as[String]).getOrElse(TrainType.Autobus),
        extractTimestampedStation(parsed, "origine", "orarioPartenza"),
        extractTimestampedStation(parsed, "destinazione", "orarioArrivo")
      ),
      (parsed \ "fermate").as[List[JsValue]]
                          .map(o => {
                            val actualDeparture = extractDatetime(o, "partenzaReale")
                            val actualArrival = extractDatetime(o, "arrivoReale")
                            Stop(
                              WordUtils.capitalizeFully((o \ "stazione").as[String]),
                              extractDatetime(o, "partenza_teorica"),
                              actualDeparture,
                              extractDatetime(o, "arrivo_teorico"),
                              actualArrival,
                              (o \ "binarioProgrammatoPartenzaDescrizione")
                                  .asOpt[String]
                                  .orElse((o \ "binarioProgrammatoArrivoDescrizione").asOpt[String]),
                              (o \ "binarioEffettivoPartenzaDescrizione")
                                  .asOpt[String]
                                  .orElse((o \ "binarioEffettivoArrivoDescrizione").asOpt[String]),
                              calculateTravelState(actualDeparture, (o \ "ritardoPartenza").as[Int]),
                              calculateTravelState(actualArrival, (o \ "ritardoArrivo").as[Int])
                            )
                          })
    )
  }
}
