package it.unibo.pcd.assignment2.eventdriven.model.parsers

import it.unibo.pcd.assignment2.eventdriven.TimeUtils
import it.unibo.pcd.assignment2.eventdriven.model.TravelState.{Delayed, Early, InTime, Nothing}
import it.unibo.pcd.assignment2.eventdriven.model._
import org.apache.commons.text.WordUtils
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDateTime

object TrainInfoParser {
  import TimeUtils._

  private def calculateTravelState(datetime: Option[LocalDateTime], delay: Int): TravelState = (datetime, delay) match {
    case (Some(_), d) if d > 0 => Delayed(d)
    case (Some(_), d) if d < 0 => Early(-d)
    case (Some(_), _) => InTime
    case _ => Nothing
  }

  private def extractDatetime(json: JsValue, key: String): Option[LocalDateTime] =
    (json \ key).asOpt[Long].map(millisToLocalDateTime)

  private def extractTimestampedStation(json: JsValue, nameKey: String, timestampKey: String): SolutionStation =
    SolutionStation(WordUtils.capitalizeFully((json \ nameKey).as[String]), (json \ timestampKey).as[Long])

  def apply(json: String): TrainInfo = {
    val parsed = Json.parse(json)
    TrainInfo(
      RouteTrain(
        (parsed \ "numeroTreno").asOpt[Int].map(_.toString),
        TrainType.values.find(_.code == (parsed \ "categoria").as[String]).getOrElse(TrainType.AUTOBUS),
        extractTimestampedStation(parsed, "origine", "orarioPartenza"),
        extractTimestampedStation(parsed, "destinazione", "orarioArrivo")
      ),
      (parsed \ "fermate").as[List[JsValue]]
                          .map(o => {
                            val actualDeparture = extractDatetime(o, "partenzaReale")
                            val actualArrival = extractDatetime(o, "arrivoReale")
                            RouteStation(
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
