package it.unibo.pcd.assignment2.eventdriven.model

sealed trait Train {
  def trainCode: Option[String]

  def trainType: TrainType.TrainType
}

object Train {
  private case class TrainImpl(trainCode: Option[String], trainType: TrainType.TrainType) extends Train

  def apply(trainCode: Option[String], trainType: TrainType.TrainType): Train = TrainImpl(trainCode, trainType)
}

sealed trait SolutionTrain extends Train {
  def departureStation: SolutionStation

  def arrivalStation: SolutionStation

  def stops: List[Stop]
}

object SolutionTrain {
  private case class SolutionTrainImpl(trainCode: Option[String],
                                       trainType: TrainType.TrainType,
                                       departureStation: SolutionStation,
                                       arrivalStation: SolutionStation,
                                       stops: List[Stop]) extends SolutionTrain

  def apply(trainCode: Option[String],
            trainType: TrainType.TrainType,
            departureStation: SolutionStation,
            arrivalStation: SolutionStation,
            stops: List[Stop]): SolutionTrain =
    SolutionTrainImpl(trainCode, trainType, departureStation, arrivalStation, stops)
}