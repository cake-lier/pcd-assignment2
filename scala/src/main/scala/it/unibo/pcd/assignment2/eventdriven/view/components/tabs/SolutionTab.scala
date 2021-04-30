package it.unibo.pcd.assignment2.eventdriven.view.components.tabs

import it.unibo.pcd.assignment2.eventdriven.model.Solution
import it.unibo.pcd.assignment2.eventdriven.view.components.Component.AbstractComponent
import it.unibo.pcd.assignment2.eventdriven.view.components.Component
import javafx.scene.control.Tab

sealed trait SolutionTab extends Component[Tab] {
 def displaySolutions(solutions: List[Solution]): Unit
}

object SolutionTab {
  import it.unibo.pcd.assignment2.eventdriven.controller.Controller
  import it.unibo.pcd.assignment2.eventdriven.model.Solution
  import it.unibo.pcd.assignment2.eventdriven.view.components.cards.SolutionCard
  import it.unibo.pcd.assignment2.eventdriven.AnyOps.discard
  import it.unibo.pcd.assignment2.eventdriven.view.components.LoadingLabel
  import javafx.fxml.FXML
  import javafx.scene.control._
  import scalafx.scene.layout.VBox

  import java.time.{LocalDateTime, LocalTime}

  private final class SolutionTabImpl(controller: Controller) extends AbstractComponent[Tab]("solutionTab.fxml")
    with SolutionTab {
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

    override val inner: Tab = loader.load[Tab]
    searchButton.setOnMouseClicked(_ => {
      solutionsField.setContent(LoadingLabel().inner)
      controller.requestSolutions(
        departureStation.getText,
        arrivalStation.getText,
        LocalDateTime.of(departureDate.getValue, LocalTime.of(Integer.parseInt(departureTime.getValue.split(":")(0)), 0))
      )
    })

    override def displaySolutions(solutions: List[Solution]): Unit = {
      val container: VBox = new VBox(5)
      discard { container.children ++= solutions.map(SolutionCard(_)) }
      solutionsField.setContent(container)
    }
  }

  def apply(controller: Controller): SolutionTab = new SolutionTabImpl(controller)
}
