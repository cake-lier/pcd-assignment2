package it.unibo.pcd.assignment2.eventdriven.view.tabs

import it.unibo.pcd.assignment2.eventdriven.controller.Controller
import it.unibo.pcd.assignment2.eventdriven.model.Solution
import it.unibo.pcd.assignment2.eventdriven.view.LoadingLabel
import it.unibo.pcd.assignment2.eventdriven.view.cards.SolutionCard
import javafx.fxml.{FXML, FXMLLoader}
import scalafx.scene.layout.VBox
import javafx.scene.control.{Button, ChoiceBox, DatePicker, ScrollPane, TextField, Tab => JavaFXTab}

import java.time.{LocalDateTime, LocalTime}

sealed trait SolutionTab extends Tab {
 def displaySolutions(solutions: List[Solution]): Unit
}

object SolutionTab {

  private class SolutionTabImpl(controller: Controller) extends SolutionTab {
    @FXML
    private var root: JavaFXTab = _
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
    loader.setLocation(ClassLoader.getSystemResource("solutionTab.fxml"))
    loader.load
    searchButton.setOnMouseClicked(_ => {
      solutionsField.setContent(LoadingLabel().label)
      controller.requestSolutions(
        departureStation.getText,
        arrivalStation.getText,
        LocalDateTime.of(departureDate.getValue, LocalTime.of(Integer.parseInt(departureTime.getValue.split(":")(0)), 0))
      )
    })

    override val tab: JavaFXTab = root

    override def displaySolutions(solutions: List[Solution]): Unit = {
      val container: VBox = new VBox(5)
      container.children.setAll(solutions.map(SolutionCard(_)).map(_.pane): _*)
      solutionsField.setContent(container)
    }
  }

  def apply(controller: Controller): SolutionTab = new SolutionTabImpl(controller)
}
