package it.unibo.pcd.assignment2.eventdriven

import java.time.LocalDateTime
import scala.concurrent.Future

object TrenitaliaAPI extends TrainsAPI {
  override type StationName = String
  override type TrainCode = String
  override type PlatformName = String
  override type TrainType = TrainTypeEnum

  class TrainTypeEnum extends Enumeration {
    type TrainType = Value
    val Regionale, RegionaleVeloce, InterCity, InterCityNotte, FrecciaBianca, FrecciaArgento, FrecciaRossa = Value
  }

  object TravelState {
    import TravelStateEnum.State

    case object NotDeparted extends TravelState {
      val state: State = TravelStateEnum.NOT_DEPARTED

      val delay: Option[Int] = None
    }

    case object Arrived extends TravelState {
      val state: State = TravelStateEnum.ARRIVED

      val delay: Option[Int] = None
    }

    case object InTime extends TravelState {
      val state: State = TravelStateEnum.IN_TIME

      val delay: Option[Int] = None
    }

    object Delayed {
      private case class Delayed(delayMin: Int) extends TravelState {
        val state: State = TravelStateEnum.DELAYED

        val delay: Option[Int] = Some(delayMin)
      }

      def apply(delay: Int): TravelState = delay match {
        case d if d > 0 => Delayed(d)
        case _ => throw new IllegalArgumentException
      }
    }

    object Early {
      private case class Early(earlyMin: Int) extends TravelState {
        val state: State = TravelStateEnum.EARLY

        val delay: Option[Int] = Some(-earlyMin)
      }

      def apply(early: Int): TravelState = early match {
        case e if e > 0 => Early(e)
        case _ => throw new IllegalArgumentException
      }
    }
  }

  override def getTrainSolutions(departureStation: StationName,
                                 arrivalStation: StationName,
                                 datetimeDeparture: LocalDateTime): Future[List[Solution]] = ???

  override def getTrainInfo(trainCode: TrainCode): Future[TrainInfo] = ???

  override def getStationInfo(stationName: StationName): Future[StationInfo] = ???

  private[TrenitaliaAPI] object Implementations {
    object Train {
      private case class TrainImpl(trainCode: TrainCode, trainType: TrainType) extends Train

      def apply(trainCode: TrainCode, trainType: TrainType): Train = TrainImpl(trainCode, trainType)
    }

    object Station {
      private case class StationImpl(stationName: StationName) extends Station

      def apply(stationName: StationName): Station = StationImpl(stationName)
    }

    object SolutionStation {
      private case class SolutionStationImpl(stationName: String, datetime: LocalDateTime) extends SolutionStation

      def apply(stationName: StationName, datetime: LocalDateTime): SolutionStation = SolutionStationImpl(stationName, datetime)
    }

    object Solution {
      private case class SolutionImpl(trains: List[Train],
                                      price: Option[Double],
                                      bookable: Boolean,
                                      saleable: Boolean,
                                      departureStation: SolutionStation,
                                      arrivalStation: SolutionStation) extends Solution

      def apply(trains: List[Train],
                price: Option[Double],
                bookable: Boolean,
                saleable: Boolean,
                departureStation: SolutionStation,
                arrivalStation: SolutionStation): Solution =
        SolutionImpl(trains: List[Train],
                     price: Option[Double],
                     bookable: Boolean,
                     saleable: Boolean,
                     departureStation: SolutionStation,
                     arrivalStation: SolutionStation)
    }

    object RouteDepartureStation {
      private case class RouteDepartureStationImpl(stationName: StationName,
                                                   plannedDatetime: LocalDateTime,
                                                   actualDatetime: Option[LocalDateTime],
                                                   plannedPlatform: PlatformName,
                                                   actualPlatform: Option[PlatformName]) extends RouteDepartureStation

      def apply(stationName: StationName,
                plannedDatetime: LocalDateTime,
                actualDatetime: Option[LocalDateTime],
                plannedPlatform: PlatformName,
                actualPlatform: Option[PlatformName]): RouteDepartureStation =
        RouteDepartureStationImpl(stationName, plannedDatetime, actualDatetime, plannedPlatform, actualPlatform)
    }

    object RouteArrivalStation {
      private case class RouteArrivalStationImpl(stationName: StationName,
                                                 plannedDatetime: LocalDateTime,
                                                 estimatedDatetime: Option[LocalDateTime],
                                                 actualDatetime: Option[LocalDateTime],
                                                 plannedPlatform: PlatformName,
                                                 actualPlatform: Option[PlatformName]) extends RouteArrivalStation

      def apply(stationName: StationName,
                plannedDatetime: LocalDateTime,
                estimatedDatetime: Option[LocalDateTime],
                actualDatetime: Option[LocalDateTime],
                plannedPlatform: PlatformName,
                actualPlatform: Option[PlatformName]): RouteArrivalStation =
        RouteArrivalStationImpl(stationName, plannedDatetime, estimatedDatetime, actualDatetime, plannedPlatform, actualPlatform)
    }

    object TrainInfo {
      private case class TrainInfoImpl(train: Train,
                                       state: TravelState,
                                       departureStation: RouteDepartureStation,
                                       arrivalStation: RouteArrivalStation) extends TrainInfo

      def apply(train: Train,
                state: TravelState,
                departureStation: RouteDepartureStation,
                arrivalStation: RouteArrivalStation): TrainInfo =
        TrainInfoImpl(train, state, departureStation, arrivalStation)
    }

    object TrainBoardRecord {
      private case class TrainBoardRecordImpl(train: Train,
                                              station: Station,
                                              state: TravelState,
                                              datetime: LocalDateTime,
                                              expectedPlatform: Option[PlatformName],
                                              actualPlatform: Option[PlatformName]) extends TrainBoardRecord

      def apply(train: Train,
                station: Station,
                state: TravelState,
                datetime: LocalDateTime,
                expectedPlatform: Option[PlatformName],
                actualPlatform: Option[PlatformName]): TrainBoardRecord =
        TrainBoardRecordImpl(train, station, state, datetime, expectedPlatform, actualPlatform)
    }

    object StationInfo {
      private case class StationInfoImpl(departures: Set[TrainBoardRecord], arrivals: Set[TrainBoardRecord]) extends StationInfo

      def apply(departures: Set[TrainBoardRecord], arrivals: Set[TrainBoardRecord]): StationInfo =
        StationInfoImpl(departures, arrivals)
    }
  }
}
