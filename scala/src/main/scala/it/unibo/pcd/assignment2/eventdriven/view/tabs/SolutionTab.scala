package it.unibo.pcd.assignment2.eventdriven.view.tabs

import it.unibo.pcd.assignment2.eventdriven.controller.Controller
import it.unibo.pcd.assignment2.eventdriven.model.Solution
import it.unibo.pcd.assignment2.eventdriven.view.LoadingLabel
import it.unibo.pcd.assignment2.eventdriven.view.cards.SolutionCard
import it.unibo.pcd.assignment2.eventdriven.AnyOps.discard
import javafx.fxml.{FXML, FXMLLoader}
import scalafx.scene.layout.VBox
import javafx.scene.control.{Button, ChoiceBox, DatePicker, ScrollPane, TextField, Tab => JavaFXTab}

import java.time.{LocalDateTime, LocalTime}

sealed trait SolutionTab extends Tab {
 def displaySolutions(solutions: List[Solution]): Unit
}

object SolutionTab {
  private final class SolutionTabImpl(controller: Controller) extends SolutionTab {
    @FXML
    private var departureStation: TextField = new TextField
    @FXML
    private var arrivalStation: TextField = new TextField
    @FXML
    private var departureDate: DatePicker = new DatePicker
    @FXML
    private var departureTime: ChoiceBox[String] = new ChoiceBox[String]
    @FXML
    private var searchButton: Button = new Button
    @FXML
    private var solutionsField: ScrollPane = new ScrollPane

    val loader = new FXMLLoader
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource("solutionTab.fxml"))
    override val tab: JavaFXTab = loader.load[JavaFXTab]
    searchButton.setOnMouseClicked(_ => {
      solutionsField.setContent(LoadingLabel().label)
      controller.requestSolutions(
        departureStation.getText,
        arrivalStation.getText,
        LocalDateTime.of(departureDate.getValue, LocalTime.of(Integer.parseInt(departureTime.getValue.split(":")(0)), 0))
      )
    })

    override def displaySolutions(solutions: List[Solution]): Unit = {
      val container: VBox = new VBox(5)
      discard { container.children ++= solutions.map(SolutionCard(_)).map(_.pane) }
      solutionsField.setContent(container)
    }
  }

  def apply(controller: Controller): SolutionTab = new SolutionTabImpl(controller)
}
