package it.unibo.pcd.assignment2.eventdriven.view

import it.unibo.pcd.assignment2.eventdriven.controller.Controller
import it.unibo.pcd.assignment2.eventdriven.model.Solution
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.Platform
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Label}
import scalafx.scene.layout.VBox

import java.time.{LocalDateTime, LocalTime}

sealed trait View {
  def displayErrorMessage(message: String): Unit

  def displaySolutions(solutions: List[Solution]): Unit
}

object View {
  private class ViewImpl(primaryStage: PrimaryStage) extends View {
    private val controller: Controller = Controller(this)
    @FXML
    private var departureStation: TextField = _
    @FXML
    private var arrivalStation: TextField = _
    @FXML
    private var departureDate: DatePicker = _
    @FXML
    private var departureTime: ChoiceBox[String] = _
    @FXML
    private var searchButton: Button = _
    @FXML
    private var solutionsField: ScrollPane = _

    val loader = new FXMLLoader
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource("main.fxml"))
    val root: TabPane = loader.load
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
    searchButton.setOnMouseClicked(_ => {
      val waitLabel = new Label("Caricamento in corso")
      waitLabel.maxWidth = Double.MaxValue
      waitLabel.maxHeight = Double.MaxValue
      waitLabel.alignment = Pos.Center
      waitLabel.setStyle("-fx-font-size: 20")
      solutionsField.setContent(waitLabel)
      controller.requestTrainSolutions(
        departureStation.getText,
        arrivalStation.getText,
        LocalDateTime.of(departureDate.getValue, LocalTime.of(Integer.parseInt(departureTime.getValue.split(":")(0)), 0))
      )
    })

    override def displayErrorMessage(message: String): Unit = Platform.runLater(new Alert(AlertType.Error) {
      initOwner(primaryStage)
      title = "Error"
      headerText = "An error has occurred"
      contentText = message
    }.showAndWait)

    override def displaySolutions(solutions: List[Solution]): Unit = Platform.runLater({
      val container: VBox = new VBox(5)
      container.children.setAll(solutions.map(SolutionCard(_)).map(_.pane): _*)
      solutionsField.setContent(container)
    })
  }

  def apply(primaryStage: PrimaryStage): View = new ViewImpl(primaryStage)
}
