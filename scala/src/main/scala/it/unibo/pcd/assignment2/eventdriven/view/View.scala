package it.unibo.pcd.assignment2.eventdriven.view

import it.unibo.pcd.assignment2.eventdriven.controller.Controller
import it.unibo.pcd.assignment2.eventdriven.model.{Solution, StationInfo, TrainInfo}
import it.unibo.pcd.assignment2.eventdriven.view.components.tabs.{SolutionTab, StationTab, TrainTab}
import it.unibo.pcd.assignment2.eventdriven.AnyOps.discard
import it.unibo.pcd.assignment2.eventdriven.view.components.Component.AbstractComponent
import javafx.scene.control._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

sealed trait View {
  def displayErrorMessage(message: String): Unit

  def displaySolutions(solutions: List[Solution]): Unit

  def displayTrainInfo(trainInfo: TrainInfo): Unit

  def suspendTrainMonitoring(): Unit

  def displayStationInfo(stationInfo: StationInfo): Unit

  def suspendStationMonitoring(): Unit
}

object View {
  private final class ViewImpl(primaryStage: PrimaryStage) extends AbstractComponent[TabPane]("main.fxml") with View {
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

    override def displayTrainInfo(trainInfo: TrainInfo): Unit = Platform.runLater(trainTab.displayTrainInfo(trainInfo))

    override def suspendTrainMonitoring(): Unit = Platform.runLater(trainTab.suspendTrainMonitoring())

    override def displayStationInfo(stationInfo: StationInfo): Unit =
      Platform.runLater(stationTab.displayStationInfo(stationInfo))

    override def suspendStationMonitoring(): Unit = Platform.runLater(stationTab.suspendStationMonitoring())
  }

  def apply(primaryStage: PrimaryStage): Unit = new ViewImpl(primaryStage)
}
