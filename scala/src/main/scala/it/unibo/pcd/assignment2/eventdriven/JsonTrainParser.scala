package it.unibo.pcd.assignment2.eventdriven

import it.unibo.pcd.assignment2.eventdriven.TrenitaliaAPI.Implementations.{Solution, SolutionStation, Train}
import it.unibo.pcd.assignment2.eventdriven.TrenitaliaAPI.{Solution, Train}
import play.api.libs.json.{JsLookupResult, JsValue, Json}
import java.time.LocalDateTime
import java.util.TimeZone


sealed trait JsonTrainParser{
  def extractSolutions(jsonString:String):List[Solution]
}

object JsonTrainParser extends JsonTrainParser {
  private val TRAIN_LIST = "trainlist"
  private val MIN_PRICE = "minprice"
  private val BOOKABLE = "bookable"
  private val SELEABLE = "saleable"
  private val ORIGIN = "origin"
  private val DESTINATION = "destination"
  private val DEPARTURE_TIME = "departuretime"
  private val ARRIVAL_TIME = "arrivaltime"
  private val TRAIN_IDENTIFIER = "trainidentifier"
  private val REGEX = "[^a-zA-Z]"

  override def extractSolutions(jsonString:String):List[Solution] =
    Json.parse(jsonString).as[List[JsValue]]
      .map(o=> Solution(
        trains = _extractTrain(o \ TRAIN_LIST),
        price = (o \ MIN_PRICE).asOpt[Double],
        bookable = (o \ BOOKABLE).as[Boolean],
        saleable = (o \ SELEABLE).as[Boolean],
        departureStation = SolutionStation((o \ ORIGIN).as[String],_millisToLocalDateTime((o \ DEPARTURE_TIME).as[Long])),
        arrivalStation = SolutionStation((o \ DESTINATION).as[String],_millisToLocalDateTime((o \ ARRIVAL_TIME).as[Long])))
      )

  import java.time.Instant
  private def _millisToLocalDateTime(millis:Long) : LocalDateTime = LocalDateTime ofInstant(Instant.ofEpochMilli(millis),TimeZone
    .getDefault.toZoneId)

  import it.unibo.pcd.assignment2.eventdriven.TrenitaliaAPI.TrainTypeEnum


  private def _extractTrain(trainJsonArray:JsLookupResult):List[Train] = {
    trainJsonArray.as[List[JsValue]].map(t=>
      Train(
        (t \ TRAIN_IDENTIFIER).as[String],
        TrainTypeEnum.trainTypes
          .find(_.name == (t \ TRAIN_IDENTIFIER).as[String].replaceAll(REGEX,"")).get))
  }
}