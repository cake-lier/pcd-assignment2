package it.unibo.pcd.assignment2.eventdriven.model

import scala.language.existentials

sealed trait TrainType {
  def code: String

  override val toString = ""
}

object TrainType {
  import scala.language.implicitConversions

  case object Regionale extends TrainType {
    val code = "REG"

    override val toString = "Regionale"
  }

  case object RegionaleVeloce extends TrainType {
    val code = "RV"

    override val toString = "Regionale Veloce"
  }

  case object Intercity extends TrainType {
    val code = "IC"

    override val toString = "Intercity"
  }

  case object IntercityNotte extends TrainType {
    val code = "ICN"

    override val toString = "IntercityNotte"
  }

  case object Frecciabianca extends TrainType {
    val code = "FB"

    override val toString = "Frecciabianca"
  }

  case object Frecciargento extends TrainType {
    val code = "FA"

    override val toString = "Frecciargento"
  }

  case object Frecciarossa extends TrainType {
    val code = "FR"

    override val toString = "Frecciarossa"
  }

  case object Eurocity extends TrainType {
    val code = "EC"

    override val toString = "Eurocity"
  }

  case object Autobus extends TrainType {
    val code = ""

    override val toString = "Autobus"
  }

  def values: Seq[TrainType] = Seq[TrainType](Regionale,
                                              RegionaleVeloce,
                                              Intercity,
                                              IntercityNotte,
                                              Frecciabianca,
                                              Frecciargento,
                                              Frecciarossa,
                                              Eurocity)
}
