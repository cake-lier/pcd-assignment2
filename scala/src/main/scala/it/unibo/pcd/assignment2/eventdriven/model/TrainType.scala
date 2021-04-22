package it.unibo.pcd.assignment2.eventdriven.model

sealed trait TrainType

object TrainType extends Enumeration with TrainType {
  type TrainType = Value
  val REGIONALE, REGIONALE_VELOCE, INTERCITY, INTERCITY_NOTTE, FRECCIABIANCA, FRECCIARGENTO, FRECCIAROSSA = Value
}
