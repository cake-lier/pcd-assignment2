package it.unibo.pcd.assignment2.eventdriven.model

/** An information about a [[Station]].
 *
 *  An entity which represents an information about a [[Station]]. At any given point in time in a [[Station]] arrive and depart
 *  [[Train]]s coming from and going to other [[Station]]s. The aggregate of this information constitutes a [[StationInfo]].
 *  The instantiation is made through its companion object.
 */
sealed trait StationInfo {
  /** Returns a list of [[TrainBoardRecord]] which contain information about the departing [[Train]]s. */
  def departures: List[TrainBoardRecord]

  /** Returns a list of [[TrainBoardRecord]] which contain information about the arriving [[Train]]s. */
  def arrivals: List[TrainBoardRecord]
}

/** Factory for new [[StationInfo]] instances. */
object StationInfo {
  /* The default implementation of a StationInfo with a case class. */
  private final case class StationInfoImpl(departures: List[TrainBoardRecord], arrivals: List[TrainBoardRecord])
      extends StationInfo

  /** Creates a new instance of the [[StationInfo]] trait.
   *
   *  @param departures the [[TrainBoardRecord]]s which contain information about the departing [[Train]]s
   *  @param arrivals the [[TrainBoardRecord]]s which contain information about the arriving [[Train]]s
   *  @return a new instance of [[StationInfo]]
   */
  def apply(departures: List[TrainBoardRecord], arrivals: List[TrainBoardRecord]): StationInfo =
    StationInfoImpl(departures, arrivals)
}
