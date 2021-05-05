package it.unibo.pcd.assignment2.eventdriven.model

/** An API for getting solutions for travelling via train and for monitoring trains and stations in real time. */
trait TrainsAPI {
  /** The type of the station names as used by the train company. */
  type StationName

  /** The type of the train codes as used by the train company. */
  type TrainCode

  /** The type of the platform names as used by the train company. */
  type PlatformName

  /** The type of trains of the train company currently circulating. */
  type TrainType

  /** The state of a [[Train]] during its travel. */
  type TravelState <: {
    /** Returns the type of this [[TravelState]]. */
    def state: TravelStateEnum

    /** Returns the delay associated with this [[TravelState]], if there is a delay. Being early is considered a negative
     *  delay.
     */
    def delay: Option[Int]
  }

  /** A [[Train]] which travels between [[Station]]s and can be monitored.
   *
   *  This is the base entity for all the others, the most generic one, so it contains only the identification elements such as
   *  the code and the type of the [[Train]].
   */
  type Train <: {
    /** Returns the code of this [[Train]]. If this is not a real [[Train]], it could also not have a code. */
    def trainCode: Option[TrainCode]

    /** Returns the type of this [[Train]]. */
    def trainType: TrainType
  }

  /** A mean of transportation as included in a [[Solution]] to reach a destination [[Station]] from an origin [[Station]].
   *
   *  A [[Transport]] entity is a specialization of a [[Train]], now provided with a context, the [[Solution]] that uses it for
   *  spanning between an origin and a destination. The departure and arrival [[Station]] are the ones at which the customer
   *  has to take or leave the [[Train]] for following the travel suggested by the [[Solution]], not the real terminal ones of the
   *  [[Train]].
   */
  type Transport <: Train {
    /** Returns the [[TimestampedStation]] from which this [[Train]] departs. */
    def departureStation: TimestampedStation

    /** Returns the [[TimestampedStation]] at which this [[Train]] arrives. */
    def arrivalStation: TimestampedStation

    /** Returns the [[Stage]]s that this [[Transport]] go through for getting from the departure [[Station]] to the arrival
     *  [[Station]].
     */
    def stages: List[Stage]
  }

  /** A [[Station]] from which a [[Train]] can depart and to which can arrive.
   *
   *  An entity which represents a generic [[Station]] and, being generic, is represented by the name of the [[Station]] itself.
   */
  type Station <: {
    /** Returns the name of this [[Station]]. */
    def stationName: StationName
  }

  import java.time.LocalDateTime

  /** A [[Station]] which is equipped with a timestamp. */
  type TimestampedStation <: Station {
    /** The timestamp associated with this [[Station]]. */
    def datetime: LocalDateTime
  }

  /** A [[Stage]] of the journey of a [[Train]] between its two terminals.
   *
   *  An entity which represents a [[Station]] which a [[Train]] is planned to arrive to and then depart from in its journey
   *  between the two [[Stage]]s which are the terminal ones. The first [[Stage]] has a departure timestamp, but not an arrival
   *  one, and the last [[Stage]] has an arrival timestamp but not a departure one.
   */
  type Stage <: Station {
    /** Returns the departure datetime at which a [[Train]] departs from this [[Stage]]. */
    def departureDatetime: Option[LocalDateTime]

    /** Returns the arrival datetime at which a [[Train]] arrives to this [[Stage]]. */
    def arrivalDatetime: Option[LocalDateTime]
  }

  /** A [[Stop]] made by a [[Train]] along its journey between its two terminals.
   *
   *  A [[Train]] is planned to arrive and depart to a series of [[Stop]]s, except for the ones which are the terminal ones, for
   *  which the [[Train]] departs and never arrives or vice-versa. It is also planned to arrive to a given platform in a given
   *  [[Stop]]. All this data can differ from the actual values, which are available only when the [[Train]] is effectively
   *  moving. Depending on when the information about a [[Train]] are requested, all these data could be available or not.
   *  Hence, all methods returns [[Option]].
   */
  type Stop <: Station {
    /** Returns the planned datetime at which a [[Train]] departs from this [[Stop]]. */
    def plannedDepartureDatetime: Option[LocalDateTime]

    /** Returns the actual datetime at which a [[Train]] departed from this [[Stop]]. */
    def actualDepartureDatetime: Option[LocalDateTime]

    /** Returns the planned datetime at which a [[Train]] arrives to this [[Stop]]. */
    def plannedArrivalDatetime: Option[LocalDateTime]

    /** Returns the actual datetime at which a [[Train]] arrived to this [[Stop]]. */
    def actualArrivalDatetime: Option[LocalDateTime]

    /** Returns the planned platform at which a [[Train]] stops at this [[Stop]]. */
    def plannedPlatform: Option[PlatformName]

    /** Returns the actual platform at which a [[Train]] stopped at this [[Stop]]. */
    def actualPlatform: Option[PlatformName]

    /** Returns the state of the travel of a [[Train]] when it departed from this [[Stop]]. */
    def departureState: TravelState

    /** Returns the state of the travel of a [[Train]] when it arrived to this [[Stop]]. */
    def arrivalState: TravelState
  }

  /** A travel solution between a departure and an arrival [[Station]].
   *
   *  An entity which represents a travel solution which allows a person to travel between a departure [[Station]] and an arrival
   *  [[Station]], starting its travel at a given datetime and ending it at another datetime. A [[Solution]] is made of
   *  [[Transport]]s which connects the departure station to the arrival station optionally stopping to intermediate [[Stage]]s. A
   *  solution can be sold and can also be booked in advance.
   */
  type Solution <: {
    /** Returns the means of transportation which are used by this [[Solution]]. */
    def transports: List[Transport]

    /** Returns the price for which this [[Solution]] is sold, if it can be sold. */
    def price: Option[Double]

    /** Returns whether or not this [[Solution]] can be booked. */
    def bookable: Boolean

    /** Returns whether or not this [[Solution]] can be sold. */
    def saleable: Boolean

    /** Returns the [[TimestampedStation]] from which the solution departs, equipped with the datetime of the departure. */
    def departureStation: TimestampedStation

    /** Returns the [[TimestampedStation]] from which the solution arrives, equipped with the datetime of the arrival. */
    def arrivalStation: TimestampedStation
  }

  /** A real-time information about a [[Train]].
   *
   *  A travelling [[Train]] follows a given route, which is identified by all [[Stop]]s in between the first and the last one.
   */
  type TrainInfo <: {
    /** Returns the [[Train]] for which getting the [[TrainInfo]]. */
    def train: Train

    /** Returns the [[Stop]]s which are made by the [[Train]] this [[TrainInfo]] is about. */
    def stops: List[Stop]
  }

  import java.time.LocalTime

  /** A record contained in the train board, which in turn can be the departure train board or the arrival train board.
   *
   *  An entity which describes an arrival or a departure - depending on the train board - of a [[Train]] in a [[Station]] at a
   *  given time in a day. The [[Train]] is planned to arrive to a given platform but can arrive to a different one, both data
   *  that can be or can not be available at the moment of the query to the system. The [[Train]] which is arriving to or
   *  departing from the [[Station]] has also a current travel state.
   */
  type TrainBoardRecord <: {
    /** Returns the [[Train]] of this [[TrainBoardRecord]]. */
    def train: Train

    /** Returns the terminal [[Station]] to which the [[Train]] is directed in this [[TrainBoardRecord]]. */
    def station: Station

    /** Returns the current state of the [[Train]] of this [[TrainBoardRecord]]. */
    def state: TravelState

    /** Returns the time at which the [[Train]] is arriving to or departing from the [[Station]] in this [[TrainBoardRecord]]. */
    def time: LocalTime

    /** Returns the platform at which the [[Train]] is expected to arrive to or to depart from in this [[TrainBoardRecord]]. */
    def expectedPlatform: Option[PlatformName]

    /** Returns the platform at which the [[Train]] arrived to or departed from in this [[TrainBoardRecord]]. */
    def actualPlatform: Option[PlatformName]
  }

  /** An information about a [[Station]].
   *
   *  An entity which represents an information about a [[Station]]. At any given point in time in a [[Station]] arrive and depart
   *  [[Train]]s coming from and going to other [[Station]]s. The aggregate of this information constitutes a [[StationInfo]].
   */
  type StationInfo <: {
    /** Returns a list of [[TrainBoardRecord]] which contain information about the departing [[Train]]s. */
    def departures: List[TrainBoardRecord]

    /** Returns a list of [[TrainBoardRecord]] which contain information about the arriving [[Train]]s. */
    def arrivals: List[TrainBoardRecord]
  }

  import io.vertx.core.Future

  /** Returns all [[Solution]]s for travelling between two [[Station]]s, given their name, departing after a given datetime.
   *
   *  The object effectively returned is a [[Future]] of the computation retrieving the [[Solution]]s, so it can be
   *  executed asynchronously with respect to the main control flow.
   *  @param departureStation the [[StationName]] from which the user wants to depart
   *  @param arrivalStation the [[StationName]] to which the user wants to arrive
   *  @param datetimeDeparture the datetime at which or after which the user wants to depart
   *  @return all [[Solution]]s satisfying the given conditions
   */
  def getTrainSolutions(departureStation: StationName,
                        arrivalStation: StationName,
                        datetimeDeparture: LocalDateTime): Future[List[Solution]]

  /** Returns the real-time information about a [[Train]] given its identifying [[TrainCode]].
   *
   *  The object effectively returned is a [[Future]] of the computation retrieving the [[Train]] information, so it can be
   *  executed asynchronously with respect to the main control flow.
   *  @param trainCode the [[TrainCode]] for the [[Train]] to monitor
   *  @return the real-time information about a [[Train]]
   */
  def getRealTimeTrainInfo(trainCode: TrainCode): Future[TrainInfo]

  /** Returns the real-time information about a [[Station]] given its identifying [[StationName]].
   *
   *  The object effectively returned is a [[Future]] of the computation retrieving the [[Station]] information, so it can be
   *  executed asynchronously with respect to the main control flow.
   *  @param stationName the [[StationName]] for the [[Station]] to monitor
   *  @return the real-time information about a [[Station]]
   */
  def getRealTimeStationInfo(stationName: StationName): Future[StationInfo]
}
