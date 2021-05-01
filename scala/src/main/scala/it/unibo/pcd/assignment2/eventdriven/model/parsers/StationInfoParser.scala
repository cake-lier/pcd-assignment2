package it.unibo.pcd.assignment2.eventdriven.model.parsers

/** A parser for creating [[it.unibo.pcd.assignment2.eventdriven.model.StationInfo]] instances. */
object StationInfoParser {
  import it.unibo.pcd.assignment2.eventdriven.AnyOps.AnyOps
  import it.unibo.pcd.assignment2.eventdriven.model.{Station, StationInfo, Train, TrainBoardRecord, TrainType, TravelState}
  import net.ruippeixotog.scalascraper.browser.JsoupBrowser
  import net.ruippeixotog.scalascraper.dsl.DSL.Extract.allText
  import net.ruippeixotog.scalascraper.dsl.DSL._
  import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList
  import org.apache.commons.text.WordUtils

  import java.time.LocalTime

  /*  Parses a simple String into a List of TrainBoardRecords, filtering them over the fact that the record is about a departure
   *  or an arrival.
   */
  private def parseList(records: List[String], departure: Boolean): List[TrainBoardRecord] = {
    val regex = ("^([A-Z]{2,3}) ([0-9]+) (Da|Per) (.+) Delle ore ([0-9]{2}:[0-9]{2}) Binario Previsto: (.+) Binario Reale: (.+)" +
                 " (in orario|ritardo [0-9]+)$").r
    records.flatMap {
      case regex(trainType, trainCode, direction, stationName, time, expectedPlatform, actualPlatform, trainState) =>
        if (direction === "Per" && !departure || direction === "Da" && departure) None else
          Some(TrainBoardRecord(
            Train(Some(trainCode), TrainType.values.find(_.code == trainType).getOrElse(TrainType.Autobus)),
            Station(WordUtils.capitalizeFully(stationName)),
            trainState match {
              case "in orario" => TravelState.InTime
              case s"ritardo $delay" => TravelState.Delayed(delay.toInt)
            },
            LocalTime.parse(time),
            Some(expectedPlatform).filter(_ != "--"),
            Some(actualPlatform).filter(_ != "--")
          ))
      case _ => None
    }
  }

  /** Parses a HTML-formatted [[String]] into a [[StationInfo]].
   *
   *  This method is built upon the Viaggiatreno HTML pages, so the format of the inputted HTML should respect the format of
   *  those HTML pages.
   *  @param dom the HTML-formatted [[String]] containing the Document Object Model from which extracting the [[StationInfo]]
   *  @return a [[StationInfo]] as parsed from the input
   */
  def apply(dom: String): StationInfo = {
    val blockClass = ".bloccorisultato"
    val records = (JsoupBrowser().parseString(dom) >> elementList(blockClass)).map(
      e => (e >> allText(blockClass)).replace(" » Vedi scheda", "")
                                     .replace("minuti", "")
                                     .split("\\s+")
                                     .filter(_.nonEmpty)
                                     .mkString(" ")
    )
    StationInfo(parseList(records, departure = true), parseList(records, departure = false))
  }
}
