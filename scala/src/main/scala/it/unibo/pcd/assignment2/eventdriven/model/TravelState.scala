package it.unibo.pcd.assignment2.eventdriven.model

sealed trait TravelState {
  def state: TravelStateEnum.State

  def delay: Option[Int]
}

object TravelState {
  import it.unibo.pcd.assignment2.eventdriven.model.TravelStateEnum.State

  case object Nothing extends TravelState {
    val state: State = TravelStateEnum.NOTHING

    val delay: Option[Int] = None
  }

  case object InTime extends TravelState {
    val state: State = TravelStateEnum.IN_TIME

    val delay: Option[Int] = None
  }

  case class Delayed(delayMin: Int) extends TravelState {
    val state: State = TravelStateEnum.DELAYED

    val delay: Option[Int] = Some(delayMin)
  }

  case class Early(earlyMin: Int) extends TravelState {
    val state: State = TravelStateEnum.EARLY

    val delay: Option[Int] = Some(-earlyMin)
  }
}
