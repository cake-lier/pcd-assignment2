package it.unibo.pcd.assignment2.eventdriven.model

/** Represents all types of [[TravelState]] which are contemplated. */
sealed trait TravelStateEnum

/** Collector for all possible values of [[TravelStateEnum]]. */
object TravelStateEnum {
  /** An unknown type of [[TravelState]], the result of applying the "null object pattern". */
  final case object Nothing extends TravelStateEnum

  /** A type of [[TravelState]] for [[Train]]s which are in time. */
  final case object InTime extends TravelStateEnum

  /** A type of [[TravelState]] for [[Train]]s which are delayed. */
  final case object Delayed extends TravelStateEnum

  /** A type of [[TravelState]] for [[Train]]s which are early. */
  final case object Early extends TravelStateEnum
}
