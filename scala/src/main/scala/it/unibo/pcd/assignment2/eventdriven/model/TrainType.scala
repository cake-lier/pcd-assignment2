package it.unibo.pcd.assignment2.eventdriven.model

sealed trait TrainType

object TrainType extends Enumeration with TrainType {
  protected case class TrainTypeVal(code: String) extends Val

  import scala.language.implicitConversions

  implicit def valueToTrainTypeVal(x: Value): TrainTypeVal = x.asInstanceOf[TrainTypeVal]

  val REGIONALE: TrainTypeVal = TrainTypeVal("REG")
  val REGIONALE_VELOCE: TrainTypeVal = TrainTypeVal("RV")
  val INTERCITY: TrainTypeVal = TrainTypeVal("IC")
  val INTERCITY_NOTTE: TrainTypeVal = TrainTypeVal("ICN")
  val FRECCIABIANCA: TrainTypeVal = TrainTypeVal("FB")
  val FRECCIARGENTO: TrainTypeVal = TrainTypeVal("FA")
  val FRECCIAROSSA: TrainTypeVal = TrainTypeVal("FR")
  val EUROCITY: TrainTypeVal = TrainTypeVal("EC")
  val AUTOBUS: TrainTypeVal = TrainTypeVal("")
}
