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
          (o \ priceKey).asOpt[Double],
          (o \ bookableKey).as[Boolean],
          (o \ saleableKey).as[Boolean],
          SolutionStation((o \ originKey).as[String], (o \ departureTimeKey).as[Long]),
          SolutionStation((o \ destinationKey).as[String], (o \ arrivalTimeKey).as[Long])
        ))
  }

  private def fromJsonToTrain(json: JsLookupResult): List[Train] = {
    val trainIdKey = "trainidentifier"
    json.as[List[JsValue]]
        .map(t => Train(
          (t \ trainIdKey).as[String],
          TrainType.values
                   .toList
                   .find(_.toString == (t \ trainIdKey).as[String].replaceAll("[^a-zA-Z]", ""))
                   .get
        ))
  }
}