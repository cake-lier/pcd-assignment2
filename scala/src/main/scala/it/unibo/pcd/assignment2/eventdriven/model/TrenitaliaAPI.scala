package it.unibo.pcd.assignment2.eventdriven.model

import io.vertx.core.Future
import it.unibo.pcd.assignment2.eventdriven.WebClient
import it.unibo.pcd.assignment2.eventdriven.model.{RouteArrivalStation => ConcreteRouteArrivalStation, RouteDepartureStation => ConcreteRouteDepartureStation, RouteStation => ConcreteRouteStation, Solution => ConcreteSolution, SolutionStation => ConcreteSolutionStation, Station => ConcreteStation, StationInfo => ConcreteStationInfo, Train => ConcreteTrain, TrainBoardRecord => ConcreteTrainBoardRecord, TrainInfo => ConcreteTrainInfo, TrainType => ConcreteTrainType, TravelState => ConcreteTravelState}

import java.time.LocalDateTime

sealed trait TrenitaliaAPI extends TrainsAPI {
  override type StationName = String
  override type TrainCode = String
  override type PlatformName = String
  override type TrainType = ConcreteTrainType
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
                                   datetimeDeparture: LocalDateTime): Future[List[Solution]] = ???

    override def getTrainInfo(trainCode: TrainCode): Future[TrainInfo] = ???

    override def getStationInfo(stationName: StationName): Future[StationInfo] = ???
  }

  def apply(webClient: WebClient): TrenitaliaAPI = new TrenitaliaAPIImpl(webClient)
}