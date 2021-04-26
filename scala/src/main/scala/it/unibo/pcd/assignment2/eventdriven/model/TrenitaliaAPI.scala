package it.unibo.pcd.assignment2.eventdriven.model

import io.vertx.core.Future
import it.unibo.pcd.assignment2.eventdriven.controller.WebClient
import it.unibo.pcd.assignment2.eventdriven.model.parsers.{SolutionsParser, TrainInfoParser, WebStationCodeFromStationNameParser, WebStationCodeFromTripParser, WebStationInfoParser}
import it.unibo.pcd.assignment2.eventdriven.model.{RouteArrivalStation => ConcreteRouteArrivalStation, RouteDepartureStation => ConcreteRouteDepartureStation, RouteStation => ConcreteRouteStation, Solution => ConcreteSolution, SolutionStation => ConcreteSolutionStation, Station => ConcreteStation, StationInfo => ConcreteStationInfo, Train => ConcreteTrain, TrainBoardRecord => ConcreteTrainBoardRecord, TrainInfo => ConcreteTrainInfo, TrainType => ConcreteTrainType, TravelState => ConcreteTravelState}

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

sealed trait TrenitaliaAPI extends TrainsAPI {
  override type StationName = String
  override type TrainCode = String
  override type PlatformName = String
  override type TrainType = ConcreteTrainType.TrainType
  override type Train = ConcreteTrain
  override type Station = ConcreteStation
  override type SolutionStation = ConcreteSolutionStation
  override type Solution = ConcreteSolution
  override type TravelState = ConcreteTravelState
  override type RouteStation = ConcreteRouteStation
  override type RouteDepartureStation = ConcreteRouteDepartureStation
  override type RouteArrivalStation = ConcreteRouteArrivalStation
  override type TrainInfo = ConcreteTrainInfo
  override type TrainBoardRecord = ConcreteTrainBoardRecord
  override type StationInfo = ConcreteStationInfo
}

object TrenitaliaAPI {
  private class TrenitaliaAPIImpl(webClient: WebClient) extends TrenitaliaAPI {
    override def getTrainSolutions(departureStation: StationName,
                                   arrivalStation: StationName,
                                   datetimeDeparture: LocalDateTime): Future[List[Solution]] = {
      val host = "www.lefrecce.it"
      val requestURI = s"/msite/api/solutions?" +
                       s"origin=${URLEncoder.encode(departureStation, StandardCharsets.UTF_8)}" +
                       s"&destination=${URLEncoder.encode(arrivalStation, StandardCharsets.UTF_8)}" +
                       s"&adate=${datetimeDeparture.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}" +
                       s"&atime=${datetimeDeparture.getHour}" +
                       "&arflag=A&adultno=1&childno=0&direction=A&frecce=false&onlyRegional=false"
      webClient.get(host, requestURI).compose(b => Future.succeededFuture(SolutionsParser.parse(b)))
    }

    override def getTrainInfo(trainCode: TrainCode): Future[TrainInfo] = {
      val viaggiatrenoHost = "www.viaggiatreno.it"


      webClient.get(viaggiatrenoHost,s"/viaggiatrenonew/resteasy/viaggiatreno/cercaNumeroTreno/$trainCode")
        .compose(c=>webClient.get(viaggiatrenoHost,s"/viaggiatrenomobile/resteasy/viaggiatreno/andamentoTreno/${WebStationCodeFromTripParser.parse(c)}/$trainCode/${System.currentTimeMillis()}"))
        .compose(r=>Future.succeededFuture(TrainInfoParser.parse(r)))
    }

    override def getStationInfo(stationName: StationName): Future[StationInfo] = {
      val viaggiatrenoHost = "www.viaggiatreno.it"

      webClient.get(viaggiatrenoHost,s"/viaggiatrenonew/resteasy/viaggiatreno/cercaStazione/$stationName")
        .compose(c=>webClient.post(viaggiatrenoHost,s"http://www.viaggiatreno.it/vt_pax_internet/mobile/stazione?codiceStazione=${WebStationCodeFromStationNameParser.parse(c)}",Map()))
        .compose(r=>{println(WebStationInfoParser.parse(r));Future.succeededFuture(WebStationInfoParser.parse(r))})
    }
  }

  def apply(webClient: WebClient): TrenitaliaAPI = new TrenitaliaAPIImpl(webClient)
}
