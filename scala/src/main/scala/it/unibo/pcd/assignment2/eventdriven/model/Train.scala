package it.unibo.pcd.assignment2.eventdriven.model

sealed trait Train {
  def trainCode: Option[String]

  def trainType: TrainType.TrainType
}

object Train {
  private case class TrainImpl(trainCode: Option[String], trainType: TrainType.TrainType) extends Train

  def apply(trainCode: Option[String], trainType: TrainType.TrainType): Train = TrainImpl(trainCode, trainType)
}