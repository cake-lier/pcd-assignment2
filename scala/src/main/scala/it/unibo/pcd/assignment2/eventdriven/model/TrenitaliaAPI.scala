package it.unibo.pcd.assignment2.eventdriven.model

import it.unibo.pcd.assignment2.eventdriven.model.{Solution => ConcreteSolution, Stage => ConcreteStage, Station => ConcreteStation, StationInfo => ConcreteStationInfo, Stop => ConcreteStop, TimestampedStation => ConcreteTimestampedStation, Train => ConcreteTrain, TrainBoardRecord => ConcreteTrainBoardRecord, TrainInfo => ConcreteTrainInfo, TrainType => ConcreteTrainType, Transport => ConcreteTransport, TravelState => ConcreteTravelState}

/** A [[TrainsAPI]] for Trenitalia trains.
 *
 *  The instantiation is made through its companion object.
 */
sealed trait TrenitaliaAPI extends TrainsAPI {
  override type StationName = String
  override type TrainCode = String
  override type PlatformName = String
  override type TrainType = ConcreteTrainType
  override type Train = ConcreteTrain
  override type Station = ConcreteStation
  override type TimestampedStation = ConcreteTimestampedStation
  override type Transport = ConcreteTransport
  override type Solution = ConcreteSolution
  override type TravelState = ConcreteTravelState
  override type Stop = ConcreteStop
  override type TrainInfo = ConcreteTrainInfo
  override type TrainBoardRecord = ConcreteTrainBoardRecord
  override type StationInfo = ConcreteStationInfo
  override type Stage = ConcreteStage
}

/** Factory for new [[TrenitaliaAPI]] instances */
object TrenitaliaAPI {
  import com.google.common.net.UrlEscapers
  import io.vertx.core.Future
  import it.unibo.pcd.assignment2.eventdriven.FutureHelpers
  import it.unibo.pcd.assignment2.eventdriven.controller.WebClient
  import play.api.libs.json.{JsArray, Json, JsValue}
  import it.unibo.pcd.assignment2.eventdriven.model.parsers.{SolutionsParser, StationCodeParser, StationInfoParser, StationNameParser, TrainInfoParser}

  import java.time.LocalDateTime
  import java.time.format.DateTimeFormatter
  import scala.util.Try

  /* The default implementation of the TrenitaliaAPI with a webclient for making requests. */
  private final class TrenitaliaAPIImpl(webClient: WebClient) extends TrenitaliaAPI {
    /* Encodes a URL in a String with the escape codes used in URL fragments. */
    private def encode(url: String): String = UrlEscapers.urlFragmentEscaper.escape(url)

    override def getTrainSolutions(departureStation: StationName,
                                   arrivalStation: StationName,
                                   datetimeDeparture: LocalDateTime): Future[List[Solution]] = {
      val host = "www.lefrecce.it"
      val solutionsURI = "/msite/api/solutions"
      val solutionsPath = solutionsURI +
                          "?" +
                          s"origin=${encode(departureStation)}" +
                          s"&destination=${encode(arrivalStation)}" +
                          s"&adate=${datetimeDeparture.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}" +
                          s"&atime=${datetimeDeparture.getHour.toString}" +
                          "&arflag=A&adultno=1&childno=0&direction=A&frecce=false&onlyRegional=false"
      val solutionIdKey = "idsolution"
      val noSolutionsMessage = "Non è stata trovata alcuna soluzione"
      webClient.get(host, solutionsPath)
               .compose(r =>
                 Try(Json.parse(r))
                   .map(s => FutureHelpers.all(s.as[List[JsValue]]
                                              .map(o => (o \ solutionIdKey).as[String])
                                              .map(i => webClient.get(host, s"$solutionsURI/$i/details"))))
                   .getOrElse(Future.failedFuture(noSolutionsMessage))
                   .compose(l => Future.succeededFuture(SolutionsParser(r, l))))
    }

    private val viaggiatrenoHost = "www.viaggiatreno.it"
    private val viaggiatrenoAPI = "/viaggiatrenonew/resteasy/viaggiatreno/"

    override def getTrainInfo(trainCode: TrainCode): Future[TrainInfo] = {
      val noTrainFoundMessage = "Il codice treno non è stato trovato"
      webClient.get(viaggiatrenoHost,s"${viaggiatrenoAPI}cercaNumeroTreno/$trainCode")
               .compose(r =>
                 Option(r)
                   .map(c => webClient.get(
                     viaggiatrenoHost,
                     s"${viaggiatrenoAPI}andamentoTreno/${StationCodeParser(c)}/$trainCode/" +
                     s"${System.currentTimeMillis().toString}"
                   ))
                   .getOrElse(Future.failedFuture(noTrainFoundMessage)))
               .compose(r => Future.succeededFuture(TrainInfoParser(r)))
    }

    override def getStationInfo(stationName: StationName): Future[StationInfo] = {
      val stationPath = s"/vt_pax_internet/mobile/stazione"
      val stationCodeFormFieldName = "codiceStazione"
      val noStationFoundMessage = "La stazione non è stata trovata"
      webClient.get(viaggiatrenoHost, s"${viaggiatrenoAPI}cercaStazione/${encode(stationName)}")
               .compose(r =>
                 Option(r)
                   .filter(s => Json.parse(s).as[JsArray].value.nonEmpty)
                   .map(s => webClient.post(
                     viaggiatrenoHost,
                     stationPath,
                     Map(stationCodeFormFieldName -> StationNameParser(s))
                   ))
                   .getOrElse(Future.failedFuture(noStationFoundMessage)))
               .compose(d => Future.succeededFuture(StationInfoParser(d)))
    }
  }

  /** Creates a new instance of the [[TrenitaliaAPI]] trait using the given [[WebClient]] for making HTTP requests.
   *
   *  @param webClient the [[WebClient]] used for the necessary HTTP requests.
   *  @return a new instance of [[TrenitaliaAPI]]
   */
  def apply(webClient: WebClient): TrenitaliaAPI = new TrenitaliaAPIImpl(webClient)
}
