package it.unibo.pcd.assignment2.eventdriven.model.parsers

import play.api.libs.json.{JsValue, Json}

/** A parser for extracting [[it.unibo.pcd.assignment2.eventdriven.model.Station]] names. */
object StationNameParser {
  /** Parses a JSON-formatted [[String]] into a [[String]] which represents a
   * [[it.unibo.pcd.assignment2.eventdriven.model.Station]] name.
   *
   *  @param json the JSON-formatted string to parse
   *  @return the extracted [[it.unibo.pcd.assignment2.eventdriven.model.Station]] name
   */
  def apply(json: String): String = {
    val stationNameKey = "id"
    Json.parse(json).as[List[JsValue]].headOption.map(o => (o \ stationNameKey).as[String]).getOrElse("")
  }
}


