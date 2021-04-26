package it.unibo.pcd.assignment2.eventdriven.model.parsers

import play.api.libs.json.{JsValue, Json}

sealed trait WebStationCodeFromStationNameParser extends Parser[String]

object WebStationCodeFromStationNameParser extends WebStationCodeFromStationNameParser {
  override def parse(json: String): String = (Json.parse(json).as[List[JsValue]].head \ "id").as[String]
}


