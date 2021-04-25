package it.unibo.pcd.assignment2.eventdriven.controller

import io.vertx.core.Future

import java.time.LocalDateTime

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
          .onSuccess(_ => {
            consumer(_)
            updates += updateKey -> getVertx.setPeriodic(
              30_000,
               _ => producer(updateKey).onSuccess(consumer(_)).onFailure(e => view.displayErrorMessage(e.getMessage))
            )
          })
          .onFailure(e => view.displayErrorMessage(e.getMessage))
      )

    override def startTrainInfoUpdates(trainCode: String): Unit =
      model.foreach(m => startUpdates(trainCode, m.getTrainInfo, view.displayTrainInfo))

    override def stopTrainInfoUpdates(trainCode: String): Unit = getVertx.cancelTimer(updates(trainCode))

    override def startStationInfoUpdates(stationName: String): Unit = {
      model.foreach(m => startUpdates(stationName, m.getStationInfo, view.displayStationInfo))
      /*view.displayStationInfo(StationInfo(Set(TrainBoardRecord(Train(Some("771"), TrainType.INTERCITY), Station("Bologna " +
          "Centrale"), TravelState.Delayed(5), LocalTime.now(), Some("3"), Some("2"))),
        Set()))*/
    }

    override def stopStationInfoUpdates(stationName: String): Unit = getVertx.cancelTimer(updates(stationName))

    override def exit(): Unit = sys.exit
  }

  def apply(view: View): Controller = new ControllerImpl(view)
}
