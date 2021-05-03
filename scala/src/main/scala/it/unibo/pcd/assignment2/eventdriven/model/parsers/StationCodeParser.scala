package it.unibo.pcd.assignment2.eventdriven.model.parsers

import play.api.libs.json.{Json, JsValue}

/** A parser for extracting [[it.unibo.pcd.assignment2.eventdriven.model.Station]] codes. */
object StationCodeParser {
  /**  Parses a JSON-formatted [[String]] into a [[String]] which represents a
   *   [[it.unibo.pcd.assignment2.eventdriven.model.Station]] code.
   *
   *  @param json the JSON-formatted string to parse
   *  @return the extracted [[it.unibo.pcd.assignment2.eventdriven.model.Station]] code
   */
  def apply(json: String): String = {
    val stationCodeKey = "codLocOrig"
    (Json.parse(json).as[JsValue] \ stationCodeKey).as[String]
  }
}
