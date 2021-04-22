package it.unibo.pcd.assignment2.eventdriven.model

object TravelStateEnum extends Enumeration {
  type State = Value

  val NOT_DEPARTED, ARRIVED, IN_TIME, DELAYED, EARLY = Value
}

sealed trait TravelState {
  def state: TravelStateEnum.State

  def delay: Option[Int]
}

object TravelState {
  import it.unibo.pcd.assignment2.eventdriven.model.TravelStateEnum.State

  case object NotDeparted extends TravelState {
    val state: State = TravelStateEnum.NOT_DEPARTED

    val delay: Option[Int] = None
  }

  case object Arrived extends TravelState {
    val state: State = TravelStateEnum.ARRIVED

    val delay: Option[Int] = None
  }

  case object InTime extends TravelState {
    val state: State = TravelStateEnum.IN_TIME

    val delay: Option[Int] = None
  }

  object Delayed {
    private case class Delayed(delayMin: Int) extends TravelState {
      val state: State = TravelStateEnum.DELAYED

      val delay: Option[Int] = Some(delayMin)
    }

    def apply(delay: Int): TravelState = delay match {
      case d if d > 0 => Delayed(d)
      case _ => throw new IllegalArgumentException
    }
  }

  object Early {
    private case class Early(earlyMin: Int) extends TravelState {
      val state: State = TravelStateEnum.EARLY

      val delay: Option[Int] = Some(-earlyMin)
    }

    def apply(early: Int): TravelState = early match {
      case e if e > 0 => Early(e)
      case _ => throw new IllegalArgumentException
    }
  }
}