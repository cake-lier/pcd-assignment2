package it.unibo.pcd.assignment2.eventdriven.model.parsers

import play.api.libs.json.{JsValue, Json}

object StationCodeParser {
  def apply(json: String): String = (Json.parse(json).as[JsValue] \ "codLocOrig").as[String]
}
