package it.unibo.pcd.assignment2.eventdriven.model

/** The state of a [[Train]] during its travel. */
sealed trait TravelState {
  /** Returns the type of this [[TravelState]]. */
  def state: TravelStateEnum

  /** Returns the delay associated with this [[TravelState]], if there is a delay. Being early is considered a negative delay. */
  def delay: Option[Int]
}

/** Collector for [[TravelState]] singletons and classes which are all types of [[TravelState]] that exists. */
object TravelState {

  /** Represents an unknown [[TravelState]], the result of applying the "null object pattern". */
  final case object Nothing extends TravelState {
    val state: TravelStateEnum = TravelStateEnum.Nothing

    val delay: Option[Int] = None
  }

  /** Represents the [[TravelState]] of a [[Train]] which is in time. */
  final case object InTime extends TravelState {
    val state: TravelStateEnum = TravelStateEnum.InTime

    val delay: Option[Int] = None
  }

  /** Represents the [[TravelState]] of a [[Train]] which is delayed. */
  final case class Delayed(delayMin: Int) extends TravelState {
    val state: TravelStateEnum = TravelStateEnum.Delayed

    val delay: Option[Int] = Some(delayMin)
  }

  /** Represents the [[TravelState]] of a [[Train]] which is early. */
  final case class Early(earlyMin: Int) extends TravelState {
    val state: TravelStateEnum = TravelStateEnum.Early

    val delay: Option[Int] = Some(-earlyMin)
  }
}
