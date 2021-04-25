package it.unibo.pcd.assignment2.eventdriven.model

import java.time.LocalTime

sealed trait TrainBoardRecord {
  def train: Train

  def station: Station

  def state: TravelState

  def time: LocalTime

  def expectedPlatform: Option[String]

  def actualPlatform: Option[String]
}

object TrainBoardRecord {
  private case class TrainBoardRecordImpl(train: Train,
                                          station: Station,
                                          state: TravelState,
                                          time: LocalTime,
                                          expectedPlatform: Option[String],
                                          actualPlatform: Option[String]) extends TrainBoardRecord

  def apply(train: Train,
            station: Station,
            state: TravelState,
            time: LocalTime,
            expectedPlatform: Option[String],
            actualPlatform: Option[String]): TrainBoardRecord =
    TrainBoardRecordImpl(train, station, state, time, expectedPlatform, actualPlatform)
}
