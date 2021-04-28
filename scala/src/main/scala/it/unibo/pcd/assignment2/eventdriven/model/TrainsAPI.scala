package it.unibo.pcd.assignment2.eventdriven.model

trait TrainsAPI {
  type StationName
  type TrainCode
  type PlatformName
  type TrainType

  type TravelState <: {
    def state: TravelStateEnum.State

    def delay: Option[Int]
  }

  type Train <: {
    def trainCode: Option[TrainCode]

    def trainType: TrainType
  }

  type RouteTrain <: Train {
    def departureStation: SolutionStation

    def arrivalStation: SolutionStation
  }

  type SolutionTrain <: RouteTrain {
    def stops: List[Stop]
  }

  type Station <: {
    def stationName: StationName
  }

  import java.time.LocalDateTime

  type SolutionStation <: Station {
    def datetime: LocalDateTime
  }

  type Stop <: Station {
    def departureDatetime: Option[LocalDateTime]

    def arrivalDatetime: Option[LocalDateTime]
  }

  type RouteStation <: Station {
    def plannedDepartureDatetime: Option[LocalDateTime]

    def actualDepartureDatetime: Option[LocalDateTime]

    def plannedArrivalDatetime: Option[LocalDateTime]

    def actualArrivalDatetime: Option[LocalDateTime]

    def plannedPlatform: Option[PlatformName]

    def actualPlatform: Option[PlatformName]

    def departureState: TravelState

    def arrivalState: TravelState
  }

  type Solution <: {
    def trains: List[SolutionTrain]

    def price: Option[Double]

    def bookable: Boolean

    def saleable: Boolean

    def departureStation: SolutionStation

    def arrivalStation: SolutionStation
  }

  type TrainInfo <: {
    def train: RouteTrain

    def stations: List[RouteStation]
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
    def departures: List[TrainBoardRecord]

    def arrivals: List[TrainBoardRecord]
  }

  import io.vertx.core.Future

  def getTrainSolutions(departureStation: StationName,
                        arrivalStation: StationName,
                        datetimeDeparture: LocalDateTime): Future[List[Solution]]

  def getTrainInfo(trainCode: TrainCode): Future[TrainInfo]

  def getStationInfo(stationName: StationName): Future[StationInfo]
}
