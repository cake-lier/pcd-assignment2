package it.unibo.pcd.assignment2.eventdriven.model

sealed trait TravelStateEnum

object TravelStateEnum {
  case object Nothing extends TravelStateEnum
  case object InTime extends TravelStateEnum
  case object Delayed extends TravelStateEnum
  case object Early extends TravelStateEnum
}
