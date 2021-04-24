package it.unibo.pcd.assignment2.eventdriven.controller

import io.vertx.core.Future
import it.unibo.pcd.assignment2.eventdriven.model.{StationInfo, TrainInfo, TrenitaliaAPI}
import it.unibo.pcd.assignment2.eventdriven.view.View

import java.time.LocalDateTime

sealed trait Controller {
  def requestTrainSolutions(departureStation: String, arrivalStation: String, datetimeDeparture: LocalDateTime): Unit

  def getTrainInfo(trainCode: String): Future[TrainInfo]

  def getStationInfo(stationName: String): Future[StationInfo]

  def exit(): Unit
}

object Controller {
  import io.vertx.core.{AbstractVerticle, Promise, Vertx}

  private class ControllerImpl(view: View) extends AbstractVerticle with Controller {
    var model: Option[TrenitaliaAPI] = None

    Vertx.vertx().deployVerticle(this)

    override def start(startPromise: Promise[Void]): Unit = {
      model = Some(TrenitaliaAPI(WebClient(getVertx)))
    }

    override def requestTrainSolutions(departureStation: String, arrivalStation: String, datetimeDeparture: LocalDateTime): Unit =
      model.map(_.getTrainSolutions(departureStation, arrivalStation, datetimeDeparture))
           .foreach(_.onSuccess(view.displaySolutions(_)).onFailure(e => view.displayErrorMessage(e.getMessage)))

    override def getTrainInfo(trainCode: String): Future[TrainInfo] = model.get.getTrainInfo(trainCode)

    override def getStationInfo(stationName: String): Future[StationInfo] = model.get.getStationInfo(stationName)

    override def exit(): Unit = sys.exit
  }

  def apply(view: View): Controller = new ControllerImpl(view)
}
