package it.unibo.pcd.assignment2.eventdriven.model.parsers


import it.unibo.pcd.assignment2.eventdriven.model.parsers.TrainInfoParser.{TrainTypeMap, schifo}
import it.unibo.pcd.assignment2.eventdriven.model.{Station, StationInfo, Train, TrainBoardRecord, TravelState}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import org.apache.commons.text.WordUtils

import java.time.LocalTime

sealed trait WebStationInfoParser extends Parser[StationInfo]


object WebStationInfoParser extends WebStationInfoParser{

  override def parse(dom: String): StationInfo = {
    var arrivals: Set[TrainBoardRecord] = Set()
    var departures: Set[TrainBoardRecord] = Set()

    def _createTrain(trainCode: String, trainType: String, station: String, trainState: TravelState, time: String, expPlatform: String, realPlatform: String) =
        TrainBoardRecord(Train(Some(trainCode), TrainTypeMap.map(trainType)), Station(WordUtils.capitalize(station)), trainState, LocalTime.parse(time), Some(expPlatform), _getPlatform(realPlatform))

    def _getPlatform(realPlatform: String): Option[String] = if (realPlatform == "--") None else Some(realPlatform)

    (JsoupBrowser().parseString(dom) >> elementList(".bloccorisultato"))
      .map(e => (e >> allText(".bloccorisultato"))
        .replace(" » Vedi scheda", "")
        .replace("minuti", "")
        .split("\\s+")
        .filter(_.nonEmpty)
        .toList.mkString(" "))
      .foreach {
        case s"${t} ${n} ${d} ${s} Delle ore ${o} Binario Previsto: ${bp} Binario Reale: ${br} ritardo ${r}" => d match {
          case "Da" => arrivals += _createTrain(n, t, s, TravelState.Delayed(r.toInt), o, bp, br)
          case "Per" => departures += _createTrain(n, t, s, TravelState.Delayed(r.toInt), o, bp, br)
        }
        case s"${t} ${n} ${d} ${s} Delle ore ${o} Binario Previsto: ${bp} Binario Reale: ${br} in orario" => d match {
          case "Da" =>arrivals += _createTrain(n, t, s, TravelState.InTime, o, bp, br)
          case "Per" =>departures += _createTrain(n, t, s, TravelState.InTime, o, bp, br)
        }
      }
    StationInfo(departures, arrivals)
  }
}
