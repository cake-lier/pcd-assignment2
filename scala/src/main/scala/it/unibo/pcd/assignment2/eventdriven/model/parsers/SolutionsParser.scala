package it.unibo.pcd.assignment2.eventdriven.model.parsers

import it.unibo.pcd.assignment2.eventdriven.TimeUtils.fromMillisToLocalDateTime
import it.unibo.pcd.assignment2.eventdriven.model.{Solution, SolutionStation, Train, TrainType}

sealed trait SolutionsParser extends JsonParser[List[Solution]]

object SolutionsParser extends SolutionsParser {
  import play.api.libs.json.{JsLookupResult, JsValue, Json}

  override def parse(json: String): List[Solution] = {
    val trainListKey = "trainlist"
    val priceKey = "minprice"
    val bookableKey = "bookable"
    val saleableKey = "saleable"
    val originKey = "origin"
    val destinationKey = "destination"
    val departureTimeKey = "departuretime"
    val arrivalTimeKey = "arrivaltime"
    Json.parse(json)
        .as[List[JsValue]]
        .map(o => Solution(
          fromJsonToTrain(o \ trainListKey),
          Some((o \ priceKey).as[Double]).filter(_ > 0),
          (o \ saleableKey).as[Boolean],
          SolutionStation((o \ originKey).as[String], (o \ departureTimeKey).as[Long]),
          SolutionStation((o \ destinationKey).as[String], (o \ arrivalTimeKey).as[Long])
        ))
  }

  private def fromJsonToTrain(json: JsLookupResult): List[Train] = {
    val trainIdKey = "trainidentifier"
    val trainCodePattern = ".* ([0-9]+)$".r
    val trainTypePattern = "^([a-zA-Z]+ [a-zA-Z]+|[a-zA-Z]+) .*".r
    json.as[List[JsValue]]
        .map(t => Train(
          (t \ trainIdKey).as[String] match {
            case trainCodePattern(trainCode) => Some(trainCode)
            case _ => None
          },
          (t \ trainIdKey).as[String] match {
            case trainTypePattern(trainType) =>
              TrainType.values.toList.find(_.toString == trainType.replace(" ", "_").toUpperCase).getOrElse(TrainType.AUTOBUS)
            case _ => TrainType.AUTOBUS
          }
        ))
  }
}