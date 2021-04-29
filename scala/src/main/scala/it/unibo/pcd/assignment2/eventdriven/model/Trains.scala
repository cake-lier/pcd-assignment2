package it.unibo.pcd.assignment2.eventdriven.model

sealed trait Train {
  def trainCode: Option[String]

  def trainType: TrainType
}

object Train {
  private final case class TrainImpl(trainCode: Option[String], trainType: TrainType) extends Train

  def apply(trainCode: Option[String], trainType: TrainType): Train = TrainImpl(trainCode, trainType)
}

sealed trait RouteTrain extends Train {
  def departureStation: SolutionStation

  def arrivalStation: SolutionStation
}

object RouteTrain {
  private final case class RouteTrainImpl(trainCode: Option[String],
                                          trainType: TrainType,
                                          departureStation: SolutionStation,
                                          arrivalStation: SolutionStation) extends RouteTrain

  def apply(trainCode: Option[String],
            trainType: TrainType,
            departureStation: SolutionStation,
            arrivalStation: SolutionStation): RouteTrain =
    RouteTrainImpl(trainCode, trainType, departureStation, arrivalStation)
}

sealed trait SolutionTrain extends RouteTrain {
  def stops: List[Stop]
}

object SolutionTrain {
  private final case class SolutionTrainImpl(trainCode: Option[String],
                                             trainType: TrainType,
                                             departureStation: SolutionStation,
                                             arrivalStation: SolutionStation,
                                             stops: List[Stop]) extends SolutionTrain

  def apply(trainCode: Option[String],
            trainType: TrainType,
            departureStation: SolutionStation,
            arrivalStation: SolutionStation,
            stops: List[Stop]): SolutionTrain =
    SolutionTrainImpl(trainCode, trainType, departureStation, arrivalStation, stops)
}