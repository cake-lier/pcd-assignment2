package it.unibo.pcd.assignment2.eventdriven.view

import it.unibo.pcd.assignment2.eventdriven.controller.Controller
import it.unibo.pcd.assignment2.eventdriven.model.{Solution, StationInfo, TrainInfo}
import it.unibo.pcd.assignment2.eventdriven.view.tabs.{SolutionTab, StationTab, TrainTab}
import javafx.fxml.FXMLLoader
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

  def displayStationInfo(stationInfo: StationInfo): Unit
}

object View {
  private class ViewImpl(primaryStage: PrimaryStage) extends View {
    private val controller: Controller = Controller(this)
    private val solutionTab: SolutionTab = SolutionTab(controller)
    private val trainTab: TrainTab = TrainTab(controller)
    private val stationTab: StationTab = StationTab(controller)

    val loader = new FXMLLoader
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource("main.fxml"))
    val root: TabPane = loader.load
    root.getTabs.setAll(solutionTab.tab, trainTab.tab, stationTab.tab)
    val scene = new Scene
    scene.root.value = root
    primaryStage.scene = scene
    primaryStage.sizeToScene
    primaryStage.title = "Monitoraggio Trenitalia"
    primaryStage.setOnCloseRequest(_ => controller.exit())
    primaryStage.show
    primaryStage.centerOnScreen
    primaryStage.setMinWidth(primaryStage.getWidth)
    primaryStage.setMinHeight(primaryStage.getHeight)

    override def displayErrorMessage(message: String): Unit = Platform.runLater(new Alert(AlertType.Error) {
      initOwner(primaryStage)
      title = "Error"
      headerText = "An error has occurred"
      contentText = message
    }.showAndWait)

    override def displaySolutions(solutions: List[Solution]): Unit = solutionTab.displaySolutions(solutions)

    override def displayTrainInfo(trainInfo: TrainInfo): Unit = trainTab.displayTrainInfo(trainInfo)

    override def displayStationInfo(stationInfo: StationInfo): Unit = stationTab.displayStationInfo(stationInfo)
  }

  def apply(primaryStage: PrimaryStage): View = new ViewImpl(primaryStage)
}
