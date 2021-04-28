package it.unibo.pcd.assignment2.eventdriven.model

object TravelStateEnum extends Enumeration {
  type State = Value

  val NOTHING, IN_TIME, DELAYED, EARLY = Value
}
