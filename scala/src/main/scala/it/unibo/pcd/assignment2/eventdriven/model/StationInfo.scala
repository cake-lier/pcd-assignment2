package it.unibo.pcd.assignment2.eventdriven.model

sealed trait StationInfo {
  def departures: List[TrainBoardRecord]

  def arrivals: List[TrainBoardRecord]
}

object StationInfo {
  private case class StationInfoImpl(departures: List[TrainBoardRecord], arrivals: List[TrainBoardRecord]) extends StationInfo

  def apply(departures: List[TrainBoardRecord], arrivals: List[TrainBoardRecord]): StationInfo =
    StationInfoImpl(departures, arrivals)
}
