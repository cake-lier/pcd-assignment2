package it.unibo.pcd.assignment2.eventdriven

trait TrainsAPI {
  type StationName
  type TrainCode
  type PlatformName
  type TrainType <: Enumeration

  trait Train {
    def trainCode: TrainCode

    def trainType: TrainType
  }

  trait Station {
    def stationName: StationName
  }

  import java.time.{Duration, LocalDateTime}
  
  trait SolutionStation extends Station {
    def datetime: LocalDateTime
  }

  trait Solution {
    def trains: List[Train]

    def price: Option[Double]

    def bookable: Boolean

    def saleable: Boolean

    def departureStation: SolutionStation

    def arrivalStation: SolutionStation

    def totalTravelTime: Duration =
      Duration.between(this.departureStation.datetime, this.arrivalStation.datetime)
  }

  object TravelStateEnum extends Enumeration {
    type State = Value

    val NOT_DEPARTED, ARRIVED, IN_TIME, DELAYED, EARLY = Value
  }

  trait TravelState {
    import TravelStateEnum.State

    def state: State

    def delay: Option[Int]
  }

  sealed trait RouteStation extends Station {
    def plannedDatetime: LocalDateTime

    def actualDatetime: Option[LocalDateTime]

    def plannedPlatform: PlatformName

    def actualPlatform: Option[PlatformName]
  }

  trait RouteDepartureStation extends RouteStation

  trait RouteArrivalStation extends RouteStation {
    def estimatedDatetime: Option[LocalDateTime]
  }

  trait TrainInfo {
    def train: Train

    def state: TravelState

    def departureStation: RouteDepartureStation

    def arrivalStation: RouteArrivalStation
  }

  trait TrainBoardRecord {
    def train: Train

    def station: Station

    def state: TravelState

    def datetime: LocalDateTime

    def expectedPlatform: Option[PlatformName]

    def actualPlatform: Option[PlatformName]
  }

  trait StationInfo {
    def departures: Set[TrainBoardRecord]

    def arrivals: Set[TrainBoardRecord]
  }

  import scala.concurrent.Future

  def getTrainSolutions(departureStation: StationName,
              arrivalStation: StationName,
              datetimeDeparture: LocalDateTime): Future[List[Solution]]

  def getTrainInfo(trainCode: TrainCode): Future[TrainInfo]

  def getStationInfo(stationName: StationName): Future[StationInfo]
}
