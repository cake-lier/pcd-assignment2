package it.unibo.pcd.assignment2.eventdriven.view

import it.unibo.pcd.assignment2.eventdriven.model.{Solution, StationInfo, TrainInfo}

/** The View component of this application, as intended in the "Model-View-Controller" pattern.
 *
 *  This class is a representation of the View component of the "MVC" pattern for this application. As such, it is a
 *  representation in code of the interface exposed to the user of the application. It lets the user interact with it and, as
 *  such, notify it of what it wants to do or to change in the application state, notifications that are then relayed to the
 *  Controller component. It is also notified about events that changed the Model state so as to display them to the user.
 *  The instantiation is made through its companion object.
 */
sealed trait View {
  /** Displays the given message as an error message in the GUI.
   *
   *  @param message the message to display
   */
  def displayErrorMessage(message: String): Unit

  /** Displays a list of [[Solution]]s in the GUI.
   *
   *  @param solutions the [[Solution]]s to display
   */
  def displaySolutions(solutions: List[Solution]): Unit

  /** Resets the display of [[Solution]]s. */
  def resetSolutions(): Unit

  /** Displays a new [[TrainInfo]] information in the GUI.
   *
   *  @param trainInfo the [[TrainInfo]] to display
   */
  def displayTrainInfo(trainInfo: TrainInfo): Unit

  /** Displays the monitoring of [[TrainInfo]]s as suspended. */
  def suspendTrainMonitoring(): Unit

  /** Displays a new [[StationInfo]] information in the GUI.
   *
   *  @param stationInfo the [[StationInfo]] to display
   */
  def displayStationInfo(stationInfo: StationInfo): Unit

  /** Displays the monitoring of [[StationInfo]]s as suspended. */
  def suspendStationMonitoring(): Unit
}

/** Factory for creating new [[View]] instances. */
object View {
  import it.unibo.pcd.assignment2.eventdriven.controller.Controller
  import it.unibo.pcd.assignment2.eventdriven.model.{Solution, StationInfo, TrainInfo}
  import it.unibo.pcd.assignment2.eventdriven.view.components.tabs.{SolutionTab, StationTab, TrainTab}
  import it.unibo.pcd.assignment2.eventdriven.AnyOps.discard
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component.AbstractComponent
  import javafx.scene.control.TabPane
  import scalafx.application.JFXApp.PrimaryStage
  import scalafx.application.Platform
  import scalafx.scene.Scene
  import scalafx.scene.control.Alert
  import scalafx.scene.control.Alert.AlertType

  /* The default View implementation through JavaFX. */
  private final class ViewImpl(primaryStage: PrimaryStage)
    extends AbstractComponent[TabPane](fxmlFileName = "main.fxml") with View {

    private val controller: Controller = Controller(this)
    private val solutionTab: SolutionTab = SolutionTab(controller)
    private val trainTab: TrainTab = TrainTab(controller)
    private val stationTab: StationTab = StationTab(controller)
    override val inner: TabPane = loader.load[TabPane]

    discard { inner.getTabs.setAll(solutionTab, trainTab, stationTab) }
    val scene = new Scene
    scene.root.value = inner
    primaryStage.scene = scene
    primaryStage.sizeToScene()
    primaryStage.title = "Monitoraggio Trenitalia"
    primaryStage.setOnCloseRequest(_ => controller.exit())
    primaryStage.show()
    primaryStage.centerOnScreen()
    primaryStage.setMinWidth(primaryStage.getWidth)
    primaryStage.setMinHeight(primaryStage.getHeight)

    override def displayErrorMessage(message: String): Unit = Platform.runLater(new Alert(AlertType.Error) {
      initOwner(primaryStage)
      title = "Errore"
      headerText = "Si è verificato un errore"
      contentText = message
    }.showAndWait())

    override def displaySolutions(solutions: List[Solution]): Unit = Platform.runLater(solutionTab.displaySolutions(solutions))

    override def resetSolutions(): Unit = Platform.runLater(solutionTab.resetSolutions())

    override def displayTrainInfo(trainInfo: TrainInfo): Unit = Platform.runLater(trainTab.displayTrainInfo(trainInfo))

    override def suspendTrainMonitoring(): Unit = Platform.runLater(trainTab.suspendTrainMonitoring())

    override def displayStationInfo(stationInfo: StationInfo): Unit =
      Platform.runLater(stationTab.displayStationInfo(stationInfo))

    override def suspendStationMonitoring(): Unit = Platform.runLater(stationTab.suspendStationMonitoring())
  }

  /** Creates a new instance of the [[View]] component, but does not return it.
   *
   *  The View, which is the first component created in the application, is self-sufficient and creates all the remaining
   *  components of the application. It does not need to be passed around.
   *  @param primaryStage the primary stage created by JavaFX and to be used by this View component
   */
  def apply(primaryStage: PrimaryStage): Unit = new ViewImpl(primaryStage)
}
