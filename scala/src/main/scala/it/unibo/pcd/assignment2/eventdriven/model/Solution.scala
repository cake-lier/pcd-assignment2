package it.unibo.pcd.assignment2.eventdriven.model

import java.time.Duration

sealed trait Solution {
  def trains: List[SolutionTrain]

  def price: Option[Double]

  def bookable: Boolean

  def saleable: Boolean

  def departureStation: SolutionStation

  def arrivalStation: SolutionStation

  def totalTravelTime: Duration = Duration.between(this.departureStation.datetime, this.arrivalStation.datetime)
}

object Solution {
  private final case class SolutionImpl(trains: List[SolutionTrain],
                                        price: Option[Double],
                                        bookable: Boolean,
                                        saleable: Boolean,
                                        departureStation: SolutionStation,
                                        arrivalStation: SolutionStation) extends Solution

  def apply(trains: List[SolutionTrain],
            price: Option[Double],
            bookable: Boolean,
            saleable: Boolean,
            departureStation: SolutionStation,
            arrivalStation: SolutionStation): Solution =
    SolutionImpl(trains, price, bookable, saleable, departureStation, arrivalStation)
}
