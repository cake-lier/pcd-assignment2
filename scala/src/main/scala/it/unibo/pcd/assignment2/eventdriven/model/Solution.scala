package it.unibo.pcd.assignment2.eventdriven.model

import java.time.Duration

sealed trait Solution {
  def trains: List[Train]

  def price: Option[Double]

  def bookable: Boolean

  def departureStation: SolutionStation

  def arrivalStation: SolutionStation

  def totalTravelTime: Duration = Duration.between(this.departureStation.datetime, this.arrivalStation.datetime)
}

object Solution {
  private case class SolutionImpl(trains: List[Train],
                                  price: Option[Double],
                                  bookable: Boolean,
                                  departureStation: SolutionStation,
                                  arrivalStation: SolutionStation) extends Solution

  def apply(trains: List[Train],
            price: Option[Double],
            bookable: Boolean,
            departureStation: SolutionStation,
            arrivalStation: SolutionStation): Solution =
    SolutionImpl(trains: List[Train],
                 price: Option[Double],
                 bookable: Boolean,
                 departureStation: SolutionStation,
                 arrivalStation: SolutionStation)
}
