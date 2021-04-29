package it.unibo.pcd.assignment2.eventdriven.model.parsers

import play.api.libs.json.{JsValue, Json}

object StationNameParser {
   def apply(json: String): String = Json.parse(json)
                                         .as[List[JsValue]]
                                         .headOption
                                         .map(o => (o \ "id").as[String])
                                         .getOrElse("")
}


