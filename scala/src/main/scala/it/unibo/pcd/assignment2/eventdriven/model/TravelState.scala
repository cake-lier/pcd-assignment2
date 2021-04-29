package it.unibo.pcd.assignment2.eventdriven.model

sealed trait TravelState {
  def state: TravelStateEnum

  def delay: Option[Int]
}

object TravelState {

  case object Nothing extends TravelState {
    val state: TravelStateEnum = TravelStateEnum.Nothing

    val delay: Option[Int] = None
  }

  case object InTime extends TravelState {
    val state: TravelStateEnum = TravelStateEnum.InTime

    val delay: Option[Int] = None
  }

  final case class Delayed(delayMin: Int) extends TravelState {
    val state: TravelStateEnum = TravelStateEnum.Delayed

    val delay: Option[Int] = Some(delayMin)
  }

  final case class Early(earlyMin: Int) extends TravelState {
    val state: TravelStateEnum = TravelStateEnum.Early

    val delay: Option[Int] = Some(-earlyMin)
  }
}
