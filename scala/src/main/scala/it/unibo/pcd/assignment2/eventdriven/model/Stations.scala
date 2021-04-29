package it.unibo.pcd.assignment2.eventdriven.model

import java.time.LocalDateTime

sealed trait Station {
  def stationName: String
}

object Station {
  private final case class StationImpl(stationName: String) extends Station

  def apply(stationName: String): Station = StationImpl(stationName)
}

sealed trait SolutionStation extends Station {
  def datetime: LocalDateTime
}

object SolutionStation {
  private final case class SolutionStationImpl(stationName: String, datetime: LocalDateTime) extends SolutionStation

  def apply(stationName: String, datetime: LocalDateTime): SolutionStation = SolutionStationImpl(stationName, datetime)
}

sealed trait RouteStation extends Station {
  def plannedDepartureDatetime: Option[LocalDateTime]

  def actualDepartureDatetime: Option[LocalDateTime]

  def plannedArrivalDatetime: Option[LocalDateTime]

  def actualArrivalDatetime: Option[LocalDateTime]

  def plannedPlatform: Option[String]

  def actualPlatform: Option[String]

  def departureState: TravelState

  def arrivalState: TravelState
}

object RouteStation {
  private final case class RouteStationImpl(stationName: String,
                                            plannedDepartureDatetime: Option[LocalDateTime],
                                            actualDepartureDatetime: Option[LocalDateTime],
                                            plannedArrivalDatetime: Option[LocalDateTime],
                                            actualArrivalDatetime: Option[LocalDateTime],
                                            plannedPlatform: Option[String],
                                            actualPlatform: Option[String],
                                            departureState: TravelState,
                                            arrivalState: TravelState) extends RouteStation

  def apply(stationName: String,
            plannedDepartureDatetime: Option[LocalDateTime],
            actualDepartureDatetime: Option[LocalDateTime],
            plannedArrivalDatetime: Option[LocalDateTime],
            actualArrivalDatetime: Option[LocalDateTime],
            plannedPlatform: Option[String],
            actualPlatform: Option[String],
            departureState: TravelState,
            arrivalState: TravelState): RouteStation =
    RouteStationImpl(stationName,
                     plannedDepartureDatetime,
                     actualDepartureDatetime,
                     plannedArrivalDatetime,
                     actualArrivalDatetime,
                     plannedPlatform,
                     actualPlatform,
                     departureState,
                     arrivalState)
}

sealed trait Stop extends Station {
  def departureDatetime: Option[LocalDateTime]

  def arrivalDatetime: Option[LocalDateTime]
}

object Stop {
  private final case class StopImpl(stationName: String,
                                    departureDatetime: Option[LocalDateTime],
                                    arrivalDatetime: Option[LocalDateTime]) extends Stop

  def apply(stationName: String, departureDatetime: Option[LocalDateTime], arrivalDatetime: Option[LocalDateTime]): Stop =
    StopImpl(stationName, departureDatetime, arrivalDatetime)
}
