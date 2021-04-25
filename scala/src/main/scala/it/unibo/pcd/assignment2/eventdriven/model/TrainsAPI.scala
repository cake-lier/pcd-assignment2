package it.unibo.pcd.assignment2.eventdriven.model

trait TrainsAPI {
  type StationName
  type TrainCode
  type PlatformName
  type TrainType

  type Train <: {
    def trainCode: Option[TrainCode]

    def trainType: TrainType
  }

  type Station <: {
    def stationName: StationName
  }

  import java.time.LocalDateTime

  type SolutionStation <: Station {
    def datetime: LocalDateTime
  }

  type Solution <: {
    def trains: List[Train]

    def price: Option[Double]

    def bookable: Boolean

    def departureStation: SolutionStation

    def arrivalStation: SolutionStation
  }

  type TravelState <: {
    def state: TravelStateEnum.State

    def delay: Option[Int]
  }

  type RouteStation <: Station {
    def plannedDatetime: LocalDateTime

    def actualDatetime: Option[LocalDateTime]

    def plannedPlatform: PlatformName

    def actualPlatform: Option[PlatformName]
  }

  type RouteDepartureStation <: RouteStation

  type RouteArrivalStation <: RouteStation {
    def estimatedDatetime: Option[LocalDateTime]
  }

  type TrainInfo <: {
    def train: Train

    def state: TravelState

    def departureStation: RouteDepartureStation

    def arrivalStations: List[RouteArrivalStation]
  }

  import java.time.LocalTime

  type TrainBoardRecord <: {
    def train: Train

    def station: Station

    def state: TravelState

    def time: LocalTime

    def expectedPlatform: Option[PlatformName]

    def actualPlatform: Option[PlatformName]
  }

  type StationInfo <: {
    def departures: Set[TrainBoardRecord]

    def arrivals: Set[TrainBoardRecord]
  }

  import io.vertx.core.Future

  def getTrainSolutions(departureStation: StationName,
                        arrivalStation: StationName,
                        datetimeDeparture: LocalDateTime): Future[List[Solution]]

  def getTrainInfo(trainCode: TrainCode): Future[TrainInfo]

  def getStationInfo(stationName: StationName): Future[StationInfo]
}
