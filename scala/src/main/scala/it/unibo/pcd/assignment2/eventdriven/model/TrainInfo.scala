package it.unibo.pcd.assignment2.eventdriven.model

sealed trait TrainInfo {
  def train: RouteTrain

  def stations: List[RouteStation]
}

object TrainInfo {
  private case class TrainInfoImpl(train: RouteTrain, stations: List[RouteStation]) extends TrainInfo

  def apply(train: RouteTrain, stations: List[RouteStation]): TrainInfo = TrainInfoImpl(train, stations)
}