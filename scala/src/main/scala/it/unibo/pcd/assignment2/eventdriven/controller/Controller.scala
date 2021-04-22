package it.unibo.pcd.assignment2.eventdriven.controller

import io.vertx.core.Future
import it.unibo.pcd.assignment2.eventdriven.model.{Solution, StationInfo, TrainInfo, TrenitaliaAPI}

import java.time.LocalDateTime

sealed trait Controller {
  def getTrainSolutions(departureStation: String,
                        arrivalStation: String,
                        datetimeDeparture: LocalDateTime): Future[List[Solution]]

  def getTrainInfo(trainCode: String): Future[TrainInfo]

  def getStationInfo(stationName: String): Future[StationInfo]
}

object Controller {
  import io.vertx.core.{AbstractVerticle, Promise, Vertx}

  private class ControllerImpl extends AbstractVerticle with Controller {
    var model: Option[TrenitaliaAPI] = None

    Vertx.vertx().deployVerticle(this)

    override def start(startPromise: Promise[Void]): Unit = {
      model = Some(TrenitaliaAPI(WebClient(getVertx)))
    }

    override def getTrainSolutions(departureStation: String,
                                   arrivalStation: String,
                                   datetimeDeparture: LocalDateTime): Future[List[Solution]] =
      model.get.getTrainSolutions(departureStation, arrivalStation, datetimeDeparture)

    override def getTrainInfo(trainCode: String): Future[TrainInfo] = model.get.getTrainInfo(trainCode)

    override def getStationInfo(stationName: String): Future[StationInfo] = model.get.getStationInfo(stationName)
  }

  def apply(): Controller = new ControllerImpl
}
