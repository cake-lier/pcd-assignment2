package it.unibo.pcd.assignment2.eventdriven.controller

import java.time.LocalDateTime

/** The Controller component of this application, as intended in the "Model-View-Controller" pattern.
 *
 *  This class is a representation of the Controller component of the "MVC" pattern for this application. As such, is a
 *  representation in code of this application. It allows to manage the application state, but also to communicate with the
 *  Model component and notify it of user requests or interactions with the system. It is then notified by it of when those
 *  changes happen, so as to relay them to the View component. The View component has also the responsibility to notify the
 *  Controller when the user generates an event in the application, so as to handle it appropriately.
 *  The instantiation is made through its companion object.
 */
sealed trait Controller {
  /** Makes a request to the Model component for travel solutions between the given stations which are available after the
   *  given date and time.
   *
   *  Complying with the Trenitalia API, the given station names should be present into their system and of the given datetime
   *  only the date and the hour of the departure are considered. All the others fields are treated as they where set to 0.
   *  @param departureStation the name of the station from which departing in a solution
   *  @param arrivalStation the name of the station to which arriving in a solution
   *  @param datetimeDeparture the datetime at which or after which departing
   */
  def requestSolutions(departureStation: String, arrivalStation: String, datetimeDeparture: LocalDateTime): Unit

  /** Makes this Controller to periodically request the Model for updates about a train's information for which the code is given.
   *
   *  Complying with the Trenitalia API, the given train code should be present into their system. Multiple requests for
   *  different trains at the same time are supported. Only one series of requests per train is allowed, the violation of this
   *  constraint will result in the new request to be quietly dropped.
   *  @param trainCode the train code for which getting updates about
   */
  def startTrainInfoUpdates(trainCode: String): Unit

  /** Prevents this Controller to make further requests to the Model for updates about a train's information which the code is
   *  given.
   *
   *  Complying with the Trenitalia API, the given train code should be present into their system. If no series of requests were
   *  made before for the train which code is given, nothing will happen. After stopping a periodic series of requests, a new
   *  series could be issued for the same train again.
   *  @param trainCode the train code for which stopping the updates
   */
  def stopTrainInfoUpdates(trainCode: String): Unit

  /** Makes this Controller to periodically request the Model for updates about a station's information for which the name is
   *  given.
   *
   *  Complying with the Trenitalia API, the given station name should be present into their system. Multiple requests for
   *  different stations at the same time are supported. Only one series of requests per station is allowed, the violation of this
   *  constraint will result in the new request to be quietly dropped.
   *  @param stationName the station name for which getting updates about
   */
  def startStationInfoUpdates(stationName: String): Unit

  /** Prevents this Controller to make further requests to the Model for updates about a station's information for which the name
   *  is given.
   *
   *  Complying with the Trenitalia API, the given station name should be present into their system. If no series of requests
   *  were made before for the station which name is given, nothing will happen. After stopping a periodic series of requests, a
   *  new series could be issued for the same station again.
   *  @param stationName the station name for which stopping the updates
   */
  def stopStationInfoUpdates(stationName: String): Unit

  /** Exits this application. */
  def exit(): Unit
}

/** Factory for [[Controller]] instances. */
object Controller {
  import io.vertx.core.{AbstractVerticle, Promise, Vertx}
  import it.unibo.pcd.assignment2.eventdriven.model.TrenitaliaAPI
  import it.unibo.pcd.assignment2.eventdriven.view.View
  import io.vertx.core.Future
  import it.unibo.pcd.assignment2.eventdriven.AnyOps.discard
  import it.unibo.pcd.assignment2.eventdriven.VertxHelpers.fromFunctionToHandler

  /* Leverages a Vertx Verticle for implementing the Controller trait. */
  private final class ControllerImpl(view: View) extends AbstractVerticle with Controller {
    private var model: Option[TrenitaliaAPI] = None
    private var updates: Map[String, Long] = Map[String, Long]()
    private val delayBetweenRequests = 30_000

    discard { Vertx.vertx().deployVerticle(this) }

    override def start(startPromise: Promise[Void]): Unit = {
      model = Some(TrenitaliaAPI(WebClient(getVertx)))
    }

    override def requestSolutions(departureStation: String, arrivalStation: String, datetimeDeparture: LocalDateTime): Unit =
      model.map(_.getTrainSolutions(departureStation, arrivalStation, datetimeDeparture))
           .foreach(_.onSuccess(view.displaySolutions(_))
                     .onFailure(e => view.displayErrorMessage(e.getMessage))
                     .onFailure(_ => view.resetSolutions()))

    /* Makes this Controller to periodically request the Model for updates associated to a given key. */
    private def startUpdates[A](updateKey: String,
                                producer: String => Future[A],
                                consumer: A => Unit,
                                onError: () => Unit): Unit =
      updates.get(updateKey) match {
        case Some(_) =>
        case _ => discard {
          producer(updateKey)
            .onSuccess(consumer)
            .onSuccess(_ => updates += updateKey -> getVertx.setPeriodic(
              delayBetweenRequests,
              _ => discard { producer(updateKey).onSuccess(consumer).onFailure(e => view.displayErrorMessage(e.getMessage)) }
            ))
            .onFailure(e => view.displayErrorMessage(e.getMessage))
            .onFailure(_ => onError())
        }
      }

    /* Prevents this Controller to make further requests to the Model for updates associated to the given key. */
    private def stopUpdates(updateKey: String): Unit = updates.get(updateKey).foreach(k => {
      discard { getVertx.cancelTimer(k) }
      updates -= updateKey
    })

    override def startTrainInfoUpdates(trainCode: String): Unit =
      model.foreach(m => startUpdates(trainCode, m.getRealTimeTrainInfo, view.displayTrainInfo, view.suspendTrainMonitoring))

    override def stopTrainInfoUpdates(trainCode: String): Unit = stopUpdates(trainCode)

    override def startStationInfoUpdates(stationName: String): Unit =
      model.foreach(m => startUpdates(stationName, m.getRealTimeStationInfo, view.displayStationInfo, view.suspendStationMonitoring))

    override def stopStationInfoUpdates(stationName: String): Unit = stopUpdates(stationName)

    override def exit(): Unit = sys.exit()
  }

  /** Creates a new instance of the [[Controller]] trait.
   *
   *  @param view the [[View]] component to be notified of the results of the previously made requests
   *  @return a new instance of the [[Controller]] trait associated to the given [[View]] component
   */
  def apply(view: View): Controller = new ControllerImpl(view)
}
