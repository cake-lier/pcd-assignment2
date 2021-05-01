package it.unibo.pcd.assignment2.eventdriven.model

/** A travel solution between a departure and an arrival [[Station]].
 *
 *  An entity which represents a travel solution which allows a person to travel between a departure [[Station]] and an arrival
 *  [[Station]], starting its travel at a given datetime and ending it at another datetime. A [[Solution]] is made of
 *  [[Transport]]s which connects the departure station to the arrival station optionally stopping to intermediate [[Stage]]s. A
 *  solution can be sold and can also be booked in advance.
 *  The instantiation is made through its companion object.
 */
sealed trait Solution {
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

/** Factory for [[Solution]] instances. */
object Solution {
  /* Default implementation of the Solution trait with a case class. */
  private final case class SolutionImpl(transports: List[Transport],
                                        price: Option[Double],
                                        bookable: Boolean,
                                        saleable: Boolean,
                                        departureStation: TimestampedStation,
                                        arrivalStation: TimestampedStation) extends Solution

  /** Creates a new instance of the [[Solution]] trait.
   *
   *  @param transports the means of transportation used by this [[Solution]]
   *  @param price the price of this [[Solution]], if it can be sold
   *  @param bookable whether or not this [[Solution]] can be booked
   *  @param saleable whether or not this [[Solution]] can be sold
   *  @param departureStation the [[TimestampedStation]] from which this [[Solution]] departs
   *  @param arrivalStation the [[TimestampedStation]] to which this [[Solution]] arrives
   *  @return a new instance of [[Solution]]
   */
  def apply(transports: List[Transport],
            price: Option[Double],
            bookable: Boolean,
            saleable: Boolean,
            departureStation: TimestampedStation,
            arrivalStation: TimestampedStation): Solution =
    SolutionImpl(transports, price, bookable, saleable, departureStation, arrivalStation)
}
