package it.unibo.pcd.assignment2.eventdriven.model

/** A real-time information about a [[Train]].
 *
 *  A travelling [[Train]] follows a given [[Route]], which is identified by its first and last [[Station]] and all [[Stop]]s
 *  in between. The [[Stop]]s contains also the first and last [[Station]] which are previously mentioned.
 *  The instantiation is made through its companion object.
 */
sealed trait TrainInfo {
  /** Returns the [[Route]] for which getting the [[TrainInfo]]. */
  def route: Route

  /** Returns the [[Stop]]s which make the [[Route]] this [[TrainInfo]] is about. */
  def stops: List[Stop]
}

/** Factory for new [[TrainInfo]] instances. */
object TrainInfo {
  /* The default implementation of TrainInfo with a case class. */
  private final case class TrainInfoImpl(route: Route, stops: List[Stop]) extends TrainInfo

  /** Creates a new instance of the [[TrainInfo]] trait.
   *
   *  @param route the [[Route]] for which getting the [[TrainInfo]]
   *  @param stops the [[Stop]]s which make the [[Route]] this [[TrainInfo]] is about
   *  @return a new instance of [[TrainInfo]]
   */
  def apply(route: Route, stops: List[Stop]): TrainInfo = TrainInfoImpl(route, stops)
}