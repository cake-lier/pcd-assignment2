package it.unibo.pcd.assignment2.eventdriven.model

/** A real-time information about a [[Train]].
 *
 *  A travelling [[Train]] follows a given route, which is identified by all [[Stop]]s in between the first and the last one.
 *  The instantiation is made through its companion object.
 */
sealed trait TrainInfo {
  /** Returns the [[Train]] for which getting the [[TrainInfo]]. */
  def train: Train

  /** Returns the [[Stop]]s which are made by the [[Train]] this [[TrainInfo]] is about. */
  def stops: List[Stop]
}

/** Factory for new [[TrainInfo]] instances. */
object TrainInfo {
  /* The default implementation of TrainInfo with a case class. */
  private final case class TrainInfoImpl(train: Train, stops: List[Stop]) extends TrainInfo

  /** Creates a new instance of the [[TrainInfo]] trait.
   *
   *  @param route the [[Route]] for which getting the [[TrainInfo]]
   *  @param stops the [[Stop]]s which make the [[Route]] this [[TrainInfo]] is about
   *  @return a new instance of [[TrainInfo]]
   */
  def apply(route: Train, stops: List[Stop]): TrainInfo = TrainInfoImpl(route, stops)
}