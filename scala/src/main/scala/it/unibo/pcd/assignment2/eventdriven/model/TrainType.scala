package it.unibo.pcd.assignment2.eventdriven.model

/** A type of [[Train]] as defined by Trenitalia. */
sealed trait TrainType {
  /** Returns the code which identifies this type of [[Train]]. */
  def code: String

  /** Returns a human-readable string representation of this type of [[Train]]. */
  override val toString = ""
}

/** Collector of all possible [[TrainType]] instances. */
object TrainType {
  import scala.language.implicitConversions

  /** A "Regionale" [[Train]]. */
  final case object Regionale extends TrainType {
    val code = "REG"

    override val toString = "Regionale"
  }

  /** A "Regionale Veloce" [[Train]]. */
  final case object RegionaleVeloce extends TrainType {
    val code = "RV"

    override val toString = "Regionale Veloce"
  }

  /** An "Intercity" [[Train]]. */
  final case object Intercity extends TrainType {
    val code = "IC"

    override val toString = "Intercity"
  }

  /** An "IntercityNotte" [[Train]]. */
  final case object IntercityNotte extends TrainType {
    val code = "ICN"

    override val toString = "IntercityNotte"
  }

  /** A "Frecciabianca" [[Train]]. */
  final case object Frecciabianca extends TrainType {
    val code = "FB"

    override val toString = "Frecciabianca"
  }

  /** A "Frecciargento" [[Train]]. */
  final case object Frecciargento extends TrainType {
    val code = "FA"

    override val toString = "Frecciargento"
  }

  /** A "Frecciarossa" [[Train]]. */
  final case object Frecciarossa extends TrainType {
    val code = "FR"

    override val toString = "Frecciarossa"
  }

  /** An "Eurocity" [[Train]]. */
  final case object Eurocity extends TrainType {
    val code = "EC"

    override val toString = "Eurocity"
  }

  /** A bus used as a [[Train]] substitute by Trenitalia. */
  final case object Autobus extends TrainType {
    val code = ""

    override val toString = "Autobus"
  }

  /** A [[Seq]] of all possible values of the [[TrainType]] trait. */
  def values: Seq[TrainType] = Seq[TrainType](Regionale,
                                              RegionaleVeloce,
                                              Intercity,
                                              IntercityNotte,
                                              Frecciabianca,
                                              Frecciargento,
                                              Frecciarossa,
                                              Eurocity)
}
