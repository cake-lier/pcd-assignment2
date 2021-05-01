package it.unibo.pcd.assignment2.eventdriven.model

import java.time.LocalDateTime

/** A [[Station]] from which a [[Train]] can depart and to which can arrive.
 *
 *  An entity which represents a generic [[Station]] and, being generic, is represented by the name of the [[Station]] itself.
 *  The instantiation is made through its companion object.
 */
sealed trait Station {
  /** Returns the name of this [[Station]]. */
  def stationName: String
}

/** Factory for new [[Station]] instances. */
object Station {
  /* The default implementation of a Station with a case class. */
  private final case class StationImpl(stationName: String) extends Station

  /** Creates new instances of the [[Station]] trait.
   *
   *  @param stationName the name of the [[Station]]
   *  @return a new [[Station]] instance
   */
  def apply(stationName: String): Station = StationImpl(stationName)
}

/** A [[Station]] which is equipped with a timestamp. The instantiation is made through its companion object. */
sealed trait TimestampedStation extends Station {
  /** The timestamp associated with this [[Station]]. */
  def datetime: LocalDateTime
}

/** Factory for new [[TimestampedStation]] instances. */
object TimestampedStation {
  /* The default implementation of a TimestampedStation with a case class. */
  private final case class TimestampedStationImpl(stationName: String, datetime: LocalDateTime) extends TimestampedStation

  /** Creates new instances of the [[TimestampedStation]] trait.
   *
   *  @param stationName the name of this [[TimestampedStation]]
   *  @param datetime the timestamp associated with this [[TimestampedStation]]
   *  @return a new [[TimestampedStation]] instance
   */
  def apply(stationName: String, datetime: LocalDateTime): TimestampedStation = TimestampedStationImpl(stationName, datetime)
}

/** A [[Stop]] made by a [[Train]] along its journey between its two terminals.
 *
 *  A [[Train]] is planned to arrive and depart to a series of [[Stop]]s, except for the ones which are the terminal ones, for
 *  which the [[Train]] departs and never arrives or vice-versa. It is also planned to arrive to a given platform in a given
 *  [[Stop]]. All this data can differ from the actual values, which are available only when the [[Train]] is effectively moving.
 *  Depending on when the information about a [[Train]] are requested, all these data could be available or not. Hence, all
 *  methods returns [[Option]].
 *  The instantiation is made through its companion object.
 */
sealed trait Stop extends Station {
  /** Returns the planned datetime at which a [[Train]] departs from this [[Stop]]. */
  def plannedDepartureDatetime: Option[LocalDateTime]

  /** Returns the actual datetime at which a [[Train]] departed from this [[Stop]]. */
  def actualDepartureDatetime: Option[LocalDateTime]

  /** Returns the planned datetime at which a [[Train]] arrives to this [[Stop]]. */
  def plannedArrivalDatetime: Option[LocalDateTime]

  /** Returns the actual datetime at which a [[Train]] arrived to this [[Stop]]. */
  def actualArrivalDatetime: Option[LocalDateTime]

  /** Returns the planned platform at which a [[Train]] stops at this [[Stop]]. */
  def plannedPlatform: Option[String]

  /** Returns the actual platform at which a [[Train]] stopped at this [[Stop]]. */
  def actualPlatform: Option[String]

  /** Returns the state of the travel of a [[Train]] when it departed from this [[Stop]]. */
  def departureState: TravelState

  /** Returns the state of the travel of a [[Train]] when it arrived to this [[Stop]]. */
  def arrivalState: TravelState
}

/** Factory for new [[Stop]] instances. */
object Stop {
  /* The default Stop implementation with a case class. */
  private final case class StopImpl(stationName: String,
                                    plannedDepartureDatetime: Option[LocalDateTime],
                                    actualDepartureDatetime: Option[LocalDateTime],
                                    plannedArrivalDatetime: Option[LocalDateTime],
                                    actualArrivalDatetime: Option[LocalDateTime],
                                    plannedPlatform: Option[String],
                                    actualPlatform: Option[String],
                                    departureState: TravelState,
                                    arrivalState: TravelState) extends Stop

  /** Creates a new instance of the [[Stop]] trait.
   *
   *  @param stationName the name of this [[Stop]]
   *  @param plannedDepartureDatetime the planned datetime at which a [[Train]] departs from this [[Stop]]
   *  @param actualDepartureDatetime the actual datetime at which a [[Train]] departed from this [[Stop]]
   *  @param plannedArrivalDatetime the planned datetime at which a [[Train]] arrives to this [[Stop]]
   *  @param actualArrivalDatetime the actual datetime at which a [[Train]] arrived to this [[Stop]]
   *  @param plannedPlatform the planned platform at which a [[Train]] stops at this [[Stop]]
   *  @param actualPlatform the actual platform at which a [[Train]] stopped at this [[Stop]]
   *  @param departureState the state of the travel of a [[Train]] when it departed from this [[Stop]]
   *  @param arrivalState the state of the travel of a [[Train]] when it arrived to this [[Stop]]
   *  @return a new [[Stop]] instance
   */
  def apply(stationName: String,
            plannedDepartureDatetime: Option[LocalDateTime],
            actualDepartureDatetime: Option[LocalDateTime],
            plannedArrivalDatetime: Option[LocalDateTime],
            actualArrivalDatetime: Option[LocalDateTime],
            plannedPlatform: Option[String],
            actualPlatform: Option[String],
            departureState: TravelState,
            arrivalState: TravelState): Stop =
    StopImpl(stationName,
             plannedDepartureDatetime,
             actualDepartureDatetime,
             plannedArrivalDatetime,
             actualArrivalDatetime,
             plannedPlatform,
             actualPlatform,
             departureState,
             arrivalState)
}

/** A [[Stage]] of the journey of a [[Train]] between its two terminals.
 *
 *  An entity which represents a [[Station]] which a [[Train]] is planned to arrive to and then depart from in its journey between
 *  the two [[Stage]]s which are the terminal ones. The first [[Stage]] has a departure timestamp, but not an arrival one, and
 *  the last [[Stage]] has an arrival timestamp but not a departure one.
 *  The instantiation is made through its companion object.
 */
sealed trait Stage extends Station {
  /** Returns the departure datetime at which a [[Train]] departs from this [[Stage]]. */
  def departureDatetime: Option[LocalDateTime]

  /** Returns the arrival datetime at which a [[Train]] arrives to this [[Stage]]. */
  def arrivalDatetime: Option[LocalDateTime]
}

/** Factory for new [[Stage]] instances. */
object Stage {
  /* The default implementation of Stage with a case class. */
  private final case class StageImpl(stationName: String,
                                     departureDatetime: Option[LocalDateTime],
                                     arrivalDatetime: Option[LocalDateTime]) extends Stage

  /** Creates a new instance for the [[Stage]] trait.
   *
   *  @param stationName the name of this [[Stage]]
   *  @param departureDatetime the datetime at which a [[Train]] departs from this [[Stage]]
   *  @param arrivalDatetime the datetime at which a [[Train]] arrives to this [[Stage]]
   *  @return a new [[Stage]] instance
   */
  def apply(stationName: String, departureDatetime: Option[LocalDateTime], arrivalDatetime: Option[LocalDateTime]): Stage =
    StageImpl(stationName, departureDatetime, arrivalDatetime)
}
