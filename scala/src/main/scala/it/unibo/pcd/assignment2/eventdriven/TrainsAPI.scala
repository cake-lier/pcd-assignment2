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

    trait Solution {
        trait SolutionStation extends Station {
            def datetime: LocalDateTime
        }

        def trains: List[Train]

        def price: Option[Double]

        def bookable: Boolean

        def saleable: Boolean

        def departureStation: SolutionStation

        def arrivalStation: SolutionStation

        def totalTravelTime: Duration =
            Duration.between(this.departureStation.datetime, this.arrivalStation.datetime)
    }

    trait TravelState extends Enumeration {
        def name: String

        def delay: Option[Int]
    }

    trait TrainInfo {
        trait RouteStation extends Station {
            def plannedDatetime: LocalDateTime

            def actualDatetime: Option[LocalDateTime]

            def plannedPlatform: PlatformName

            def actualPlatform: Option[PlatformName]
        }

        trait RouteDepartureStation extends RouteStation

        trait RouteArrivalStation extends RouteStation {
            def estimatedDatetime: Option[LocalDateTime]
        }

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
