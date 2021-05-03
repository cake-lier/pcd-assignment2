package it.unibo.pcd.assignment2.eventdriven.model

/** A [[Train]] which travels between [[Station]]s and can be monitored.
 *
 *  This is the base entity for all the others, the most generic one, so it contains only the identification elements such as
 *  the code and the type of the [[Train]].
 *  The instantiation is made through its companion object.
 */
sealed trait Train {
  /** Returns the code of this [[Train]]. If this is not a real [[Train]], it could also not have a code. */
  def trainCode: Option[String]

  /** Returns the type of this [[Train]]. */
  def trainType: TrainType
}

/** Factory for new [[Train]] instances. */
object Train {
  /* The default implementation of Train with a case class. */
  private final case class TrainImpl(trainCode: Option[String], trainType: TrainType) extends Train

  /** Creates a new instance of the [[Train]] trait.
   *
   *  @param trainCode the code of this [[Train]]
   *  @param trainType the type of this [[Train]]
   *  @return a new instance of [[Train]].
   */
  def apply(trainCode: Option[String], trainType: TrainType): Train = TrainImpl(trainCode, trainType)
}

/** A mean of transportation as included in a [[Solution]] to reach a destination [[Station]] from an origin [[Station]].
 *
 *  A [[Transport]] entity is a specialization of a [[Train]], now provided with a context, the [[Solution]] that uses it for
 *  spanning between an origin and a destination. The departure and arrival [[Station]] are the ones at which the customer has to
 *  take or leave the [[Train]] for following the travel suggested by the [[Solution]], not the real terminal ones of the
 *  [[Train]].
 *  The instantiation is made through its companion object.
 */
sealed trait Transport extends Train {
  /** Returns the [[TimestampedStation]] from which this [[Transport]] starts. */
  def departureStation: TimestampedStation

  /** Returns the [[TimestampedStation]] at which this [[Transport]] ends. */
  def arrivalStation: TimestampedStation

  /** Returns the [[Stage]]s that this [[Transport]] go through for getting from the departure [[Station]] to the arrival
   *  [[Station]].
   */
  def stages: List[Stage]
}

/** Factory for new [[Transport]] instances. */
object Transport {
  /* The default Transport implementation with a case class. */
  private final case class TransportImpl(trainCode: Option[String],
                                         trainType: TrainType,
                                         departureStation: TimestampedStation,
                                         arrivalStation: TimestampedStation,
                                         stages: List[Stage]) extends Transport

  /** Creates a new instance of the [[Transport]] trait.
   *
   *  @param trainCode the code of this [[Train]]
   *  @param trainType the type of this [[Train]]
   *  @param departureStation the departure [[TimestampedStation]] for this [[Train]]
   *  @param arrivalStation the arrival [[TimestampedStation]] for this [[Train]]
   *  @param stages the [[Stage]]s of the travel of this [[Train]]
   *  @return a new [[Transport]] instance
   */
  def apply(trainCode: Option[String],
            trainType: TrainType,
            departureStation: TimestampedStation,
            arrivalStation: TimestampedStation,
            stages: List[Stage]): Transport =
    TransportImpl(trainCode, trainType, departureStation, arrivalStation, stages)
}