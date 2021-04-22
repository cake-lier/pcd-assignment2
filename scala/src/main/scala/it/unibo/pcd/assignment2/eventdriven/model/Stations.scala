package it.unibo.pcd.assignment2.eventdriven.model

import java.time.LocalDateTime

sealed trait Station {
  def stationName: String
}

object Station {
  private case class StationImpl(stationName: String) extends Station

  def apply(stationName: String): Station = StationImpl(stationName)
}

sealed trait SolutionStation extends Station {
  def datetime: LocalDateTime
}

object SolutionStation {
  private case class SolutionStationImpl(stationName: String, datetime: LocalDateTime) extends SolutionStation

  def apply(stationName: String, datetime: LocalDateTime): SolutionStation = SolutionStationImpl(stationName, datetime)
}

sealed trait RouteStation extends Station {
  def plannedDatetime: LocalDateTime

  def actualDatetime: Option[LocalDateTime]

  def plannedPlatform: String

  def actualPlatform: Option[String]
}

sealed trait RouteDepartureStation extends RouteStation

object RouteDepartureStation {
  private case class RouteDepartureStationImpl(stationName: String,
                                               plannedDatetime: LocalDateTime,
                                               actualDatetime: Option[LocalDateTime],
                                               plannedPlatform: String,
                                               actualPlatform: Option[String]) extends RouteDepartureStation

  def apply(stationName: String,
            plannedDatetime: LocalDateTime,
            actualDatetime: Option[LocalDateTime],
            plannedPlatform: String,
            actualPlatform: Option[String]): RouteDepartureStation =
    RouteDepartureStationImpl(stationName, plannedDatetime, actualDatetime, plannedPlatform, actualPlatform)
}

sealed trait RouteArrivalStation extends RouteStation {
  def estimatedDatetime: Option[LocalDateTime]
}

object RouteArrivalStation {
  private case class RouteArrivalStationImpl(stationName: String,
                                             plannedDatetime: LocalDateTime,
                                             estimatedDatetime: Option[LocalDateTime],
                                             actualDatetime: Option[LocalDateTime],
                                             plannedPlatform: String,
                                             actualPlatform: Option[String]) extends RouteArrivalStation

  def apply(stationName: String,
            plannedDatetime: LocalDateTime,
            estimatedDatetime: Option[LocalDateTime],
            actualDatetime: Option[LocalDateTime],
            plannedPlatform: String,
            actualPlatform: Option[String]): RouteArrivalStation =
    RouteArrivalStationImpl(stationName, plannedDatetime, estimatedDatetime, actualDatetime, plannedPlatform, actualPlatform)
}
