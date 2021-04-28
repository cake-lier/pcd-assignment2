package it.unibo.pcd.assignment2.eventdriven.model

import com.google.common.net.UrlEscapers
import io.vertx.core.Future
import it.unibo.pcd.assignment2.eventdriven.FutureUtils
import it.unibo.pcd.assignment2.eventdriven.controller.WebClient
import it.unibo.pcd.assignment2.eventdriven.model.parsers._
import it.unibo.pcd.assignment2.eventdriven.model.{RouteTrain => ConcreteRouteTrain, RouteStation => ConcreteRouteStation, Solution => ConcreteSolution, SolutionStation => ConcreteSolutionStation, SolutionTrain => ConcreteSolutionTrain, Station => ConcreteStation, StationInfo => ConcreteStationInfo, Stop => ConcreteStop, Train => ConcreteTrain, TrainBoardRecord => ConcreteTrainBoardRecord, TrainInfo => ConcreteTrainInfo, TrainType => ConcreteTrainType, TravelState => ConcreteTravelState}
import play.api.libs.json.JsValue

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

sealed trait TrenitaliaAPI extends TrainsAPI {
  override type StationName = String
  override type TrainCode = String
  override type PlatformName = String
  override type TrainType = ConcreteTrainType.Value
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

  private class TrenitaliaAPIImpl(webClient: WebClient) extends TrenitaliaAPI {

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
                            s"&atime=${datetimeDeparture.getHour}" +
                            "&arflag=A&adultno=1&childno=0&direction=A&frecce=false&onlyRegional=false"
      webClient.get(host, getSolutionsURI)
               .compose(b => FutureUtils.all(Json.parse(b)
                                                 .as[List[JsValue]]
                                                 .map(o => (o \ "idsolution").as[String])
                                                 .map(i => webClient.get(host, s"$solutionsURI/$i/details")))
                                        .compose(s => Future.succeededFuture(SolutionsParser(b, s))))
    }

    private val viaggiatrenoHost = "www.viaggiatreno.it"
    private val viaggiatrenoAPI = "/viaggiatrenonew/resteasy/viaggiatreno/"

    override def getTrainInfo(trainCode: TrainCode): Future[TrainInfo] = {
      webClient.get(viaggiatrenoHost,s"${viaggiatrenoAPI}cercaNumeroTreno/$trainCode")
               .compose(c => webClient.get(
                 viaggiatrenoHost,
                 s"${viaggiatrenoAPI}andamentoTreno/${StationCodeParser(c)}/$trainCode/${System.currentTimeMillis()}"
               ))
               .compose(r => Future.succeededFuture(TrainInfoParser(r)))
    }

    override def getStationInfo(stationName: StationName): Future[StationInfo] =
      webClient.get(viaggiatrenoHost,s"${viaggiatrenoAPI}cercaStazione/${encode(stationName)}")
               .compose(s => webClient.post(
                 viaggiatrenoHost,
                 s"/vt_pax_internet/mobile/stazione",
                 Map("codiceStazione" -> StationNameParser(s))
               ))
               .compose(d => Future.succeededFuture(StationInfoParser(d)))
  }

  def apply(webClient: WebClient): TrenitaliaAPI = new TrenitaliaAPIImpl(webClient)
}
