package it.unibo.pcd.assignment2.eventdriven.controller

import io.vertx.core.{Future, Handler}

import java.time.LocalDateTime
import scala.language.implicitConversions

sealed trait Controller {
  def requestSolutions(departureStation: String, arrivalStation: String, datetimeDeparture: LocalDateTime): Unit

  def startTrainInfoUpdates(trainCode: String): Unit

  def stopTrainInfoUpdates(trainCode: String): Unit

  def startStationInfoUpdates(stationName: String): Unit

  def stopStationInfoUpdates(stationName: String): Unit

  def exit(): Unit
}

object Controller {
  import io.vertx.core.{AbstractVerticle, Promise, Vertx}
  import it.unibo.pcd.assignment2.eventdriven.model.TrenitaliaAPI
  import it.unibo.pcd.assignment2.eventdriven.view.View

  private class ControllerImpl(view: View) extends AbstractVerticle with Controller {
    private var model: Option[TrenitaliaAPI] = None
    private var updates: Map[String, Long] = Map()

    Vertx.vertx().deployVerticle(this)

    implicit def fromFunctionToHandler[A](f: A => Unit): Handler[A] = f.apply

    override def start(startPromise: Promise[Void]): Unit = {
      model = Some(TrenitaliaAPI(WebClient(getVertx)))
    }

    override def requestSolutions(departureStation: String, arrivalStation: String, datetimeDeparture: LocalDateTime): Unit =
      model.map(_.getTrainSolutions(departureStation, arrivalStation, datetimeDeparture))
           .foreach(_.onSuccess(view.displaySolutions(_)).onFailure(e => view.displayErrorMessage(e.getMessage)))

    private def startUpdates[A](updateKey: String, producer: String => Future[A], consumer: A => Unit): Unit =
      updates.getOrElse(
        updateKey,
        producer(updateKey)
          .onSuccess(consumer)
          .onSuccess(_ => updates += updateKey -> getVertx.setPeriodic(
            30_000,
            _ => producer(updateKey).onSuccess(consumer).onFailure(e => view.displayErrorMessage(e.getMessage))
          ))
          .onFailure(e => view.displayErrorMessage(e.getMessage))
      )

    private def stopUpdates(updateKey: String): Unit = {
      getVertx.cancelTimer(updates(updateKey))
      updates -= updateKey
    }

    override def startTrainInfoUpdates(trainCode: String): Unit =
      model.foreach(m => startUpdates(trainCode, m.getTrainInfo, view.displayTrainInfo))

    override def stopTrainInfoUpdates(trainCode: String): Unit = stopUpdates(trainCode)

    override def startStationInfoUpdates(stationName: String): Unit =
      model.foreach(m => startUpdates(stationName, m.getStationInfo, view.displayStationInfo))

    override def stopStationInfoUpdates(stationName: String): Unit = stopUpdates(stationName)

    override def exit(): Unit = sys.exit
  }

  def apply(view: View): Controller = new ControllerImpl(view)
}
