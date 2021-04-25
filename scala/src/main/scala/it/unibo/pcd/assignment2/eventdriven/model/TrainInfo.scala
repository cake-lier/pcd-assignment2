package it.unibo.pcd.assignment2.eventdriven.model

sealed trait TrainInfo {
  def train: Train

  def state: TravelState

  def departureStation: RouteDepartureStation

  def arrivalStations: List[RouteArrivalStation]
}

object TrainInfo {
  private case class TrainInfoImpl(train: Train,
                                   state: TravelState,
                                   departureStation: RouteDepartureStation,
                                   arrivalStations: List[RouteArrivalStation]) extends TrainInfo

  def apply(train: Train,
            state: TravelState,
            departureStation: RouteDepartureStation,
            arrivalStations: List[RouteArrivalStation]): TrainInfo =
    TrainInfoImpl(train, state, departureStation, arrivalStations)
}