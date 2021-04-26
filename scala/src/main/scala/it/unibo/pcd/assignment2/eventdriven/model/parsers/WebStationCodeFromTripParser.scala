package it.unibo.pcd.assignment2.eventdriven.model.parsers

import play.api.libs.json.{JsValue, Json}

sealed trait WebStationCodeFromTripParser extends Parser[String]

object WebStationCodeFromTripParser extends WebStationCodeFromTripParser {
  override def parse(stationJson: String): String = (Json.parse(stationJson).as[JsValue] \ "codLocOrig").as[String]
}
