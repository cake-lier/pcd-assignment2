package it.unibo.pcd.assignment2.eventdriven.model

sealed trait TrainInfo {
  def train: Train

  def state: TravelState

  def departureStation: RouteDepartureStation

  def arrivalStation: RouteArrivalStation
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