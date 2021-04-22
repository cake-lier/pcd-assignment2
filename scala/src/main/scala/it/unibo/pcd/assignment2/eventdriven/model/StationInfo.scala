package it.unibo.pcd.assignment2.eventdriven.model

sealed trait StationInfo {
  def departures: Set[TrainBoardRecord]

  def arrivals: Set[TrainBoardRecord]
}

object StationInfo {
  private case class StationInfoImpl(departures: Set[TrainBoardRecord], arrivals: Set[TrainBoardRecord]) extends StationInfo

  def apply(departures: Set[TrainBoardRecord], arrivals: Set[TrainBoardRecord]): StationInfo =
    StationInfoImpl(departures, arrivals)
}
