package it.unibo.pcd.assignment2.eventdriven.model

import com.google.common.net.UrlEscapers
import io.vertx.core.Future
import it.unibo.pcd.assignment2.eventdriven.FutureUtils
import it.unibo.pcd.assignment2.eventdriven.controller.WebClient
import it.unibo.pcd.assignment2.eventdriven.model.{RouteStation => ConcreteRouteStation, RouteTrain => ConcreteRouteTrain, Solution => ConcreteSolution, SolutionStation => ConcreteSolutionStation, SolutionTrain => ConcreteSolutionTrain, Station => ConcreteStation, StationInfo => ConcreteStationInfo, Stop => ConcreteStop, Train => ConcreteTrain, TrainBoardRecord => ConcreteTrainBoardRecord, TrainInfo => ConcreteTrainInfo, TrainType => ConcreteTrainType, TravelState => ConcreteTravelState}
import it.unibo.pcd.assignment2.eventdriven.model.parsers._
import play.api.libs.json.{JsArray, JsValue}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.Try

sealed trait TrenitaliaAPI extends TrainsAPI {
  override type StationName = String
  override type TrainCode = String
  override type PlatformName = String
  override type TrainType = ConcreteTrainType
  override type Train = ConcreteTrain
  override type Station = ConcreteStation
  override type SolutionStation = ConcreteSolutionStation
  override type SolutionTrain = ConcreteSolutionTrain
  override type Solution = ConcreteSolution
  override type TravelState = ConcreteTravelState
  override type RouteStation = ConcreteRouteStation
  override type RouteTrain = ConcreteRouteTrain
  override type TrainInfo = ConcreteTrainInfo
  override type TrainBoardRecord = ConcreteTrainBoardRecord
  override type StationInfo = ConcreteStationInfo
  override type Stop = ConcreteStop
}

object TrenitaliaAPI {
  import play.api.libs.json.Json

  private final class TrenitaliaAPIImpl(webClient: WebClient) extends TrenitaliaAPI {

    private def encode(url: String): String = UrlEscapers.urlFragmentEscaper.escape(url)

    override def getTrainSolutions(departureStation: StationName,
                                   arrivalStation: StationName,
                                   datetimeDeparture: LocalDateTime): Future[List[Solution]] = {
      val host = "www.lefrecce.it"
      val solutionsURI = "/msite/api/solutions"
      val getSolutionsURI = solutionsURI +
                            "?" +
                            s"origin=${encode(departureStation)}" +
                            s"&destination=${encode(arrivalStation)}" +
                            s"&adate=${datetimeDeparture.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}" +
                            s"&atime=${datetimeDeparture.getHour.toString}" +
                            "&arflag=A&adultno=1&childno=0&direction=A&frecce=false&onlyRegional=false"
      webClient.get(host, getSolutionsURI)
               .compose(r =>
                 Try(Json.parse(r))
                   .map(s => FutureUtils.all(s.as[List[JsValue]]
                                              .map(o => (o \ "idsolution").as[String])
                                              .map(i => webClient.get(host, s"$solutionsURI/$i/details"))))
                   .getOrElse(Future.failedFuture("Non è stata trovata alcuna soluzione"))
                   .compose(l => Future.succeededFuture(SolutionsParser(r, l))))
    }

    private val viaggiatrenoHost = "www.viaggiatreno.it"
    private val viaggiatrenoAPI = "/viaggiatrenonew/resteasy/viaggiatreno/"

    override def getTrainInfo(trainCode: TrainCode): Future[TrainInfo] = {
      webClient.get(viaggiatrenoHost,s"${viaggiatrenoAPI}cercaNumeroTreno/$trainCode")
               .compose(r =>
                 Option(r)
                   .map(c => webClient.get(
                     viaggiatrenoHost,
                     s"${viaggiatrenoAPI}andamentoTreno/${StationCodeParser(c)}/$trainCode/" +
                     s"${System.currentTimeMillis().toString}"
                   ))
                   .getOrElse(Future.failedFuture("Il codice treno non è stato trovato")))
               .compose(r => Future.succeededFuture(TrainInfoParser(r)))
    }

    override def getStationInfo(stationName: StationName): Future[StationInfo] =
      webClient.get(viaggiatrenoHost,s"${viaggiatrenoAPI}cercaStazione/${encode(stationName)}")
               .compose(r =>
                 Option(r)
                   .filter(s => Json.parse(s).as[JsArray].value.nonEmpty)
                   .map(s => webClient.post(
                     viaggiatrenoHost,
                     s"/vt_pax_internet/mobile/stazione",
                     Map("codiceStazione" -> StationNameParser(s))
                   ))
                   .getOrElse(Future.failedFuture("La stazione non è stata trovata")))
               .compose(d => Future.succeededFuture(StationInfoParser(d)))
  }

  def apply(webClient: WebClient): TrenitaliaAPI = new TrenitaliaAPIImpl(webClient)
}
