package it.unibo.pcd.assignment2.eventdriven.model

import java.time.LocalTime

/** A record contained in the train board, which in turn can be the departure train board or the arrival train board.
 *
 *  An entity which describes an arrival or a departure - depending on the train board - of a [[Train]] in a [[Station]] at a
 *  given time in a day. The [[Train]] is planned to arrive to a given platform but can arrive to a different one, both data
 *  that can be or can not be available at the moment of the query to the system. The [[Train]] which is arriving to or
 *  departing from the [[Station]] has also a current travel state.
 *  The instantiation is made through its companion object.
 */
sealed trait TrainBoardRecord {
  /** Returns the [[Train]] of this [[TrainBoardRecord]]. */
  def train: Train

  /** Returns the terminal [[Station]] to which the [[Train]] is directed in this [[TrainBoardRecord]]. */
  def station: Station

  /** Returns the current state of the [[Train]] of this [[TrainBoardRecord]]. */
  def state: TravelState

  /** Returns the time at which the [[Train]] is arriving to or departing from the [[Station]] in this [[TrainBoardRecord]]. */
  def time: LocalTime

  /** Returns the platform at which the [[Train]] is expected to arrive to or to depart from in this [[TrainBoardRecord]]. */
  def expectedPlatform: Option[String]

  /** Returns the platform at which the [[Train]] arrived to or departed from in this [[TrainBoardRecord]]. */
  def actualPlatform: Option[String]
}

/** Factory for new [[TrainBoardRecord]] instances. */
object TrainBoardRecord {
  /* The default implementation of TrainBoardRecord with a case class. */
  private final case class TrainBoardRecordImpl(train: Train,
                                                station: Station,
                                                state: TravelState,
                                                time: LocalTime,
                                                expectedPlatform: Option[String],
                                                actualPlatform: Option[String]) extends TrainBoardRecord

  /** Creates a new instance of the [[TrainBoardRecord]] trait.
   *
   *  @param train the [[Train]] of this [[TrainBoardRecord]]
   *  @param station the terminal [[Station]] to which the [[Train]] is directed
   *  @param state the current state of the [[Train]]
   *  @param time the time at which the [[Train]] is arriving to or departing from the [[Station]]
   *  @param expectedPlatform the platform at which the [[Train]] is expected to arrive to or to depart from
   *  @param actualPlatform the platform at which the [[Train]] arrived to or departed from
   *  @return a new instance of [[TrainBoardRecord]]
   */
  def apply(train: Train,
            station: Station,
            state: TravelState,
            time: LocalTime,
            expectedPlatform: Option[String],
            actualPlatform: Option[String]): TrainBoardRecord =
    TrainBoardRecordImpl(train, station, state, time, expectedPlatform, actualPlatform)
}
