package it.unibo.pcd.assignment2.eventdriven.view.components.tabs

import it.unibo.pcd.assignment2.eventdriven.model.Solution
import it.unibo.pcd.assignment2.eventdriven.view.components.Component
import javafx.scene.control.Tab

/** A tab [[Component]] for querying and displaying travel [[Solution]]s.
 *
 *  This [[Component]] displays the controls for getting [[Solution]]s from the backend and display them. The View component
 *  delegates all those responsibilities to this [[Component]] so as to better encapsulate its behavior.
 *  The instantiation is made through its companion object.
 */
sealed trait SolutionTab extends Component[Tab] {
  /** Displays a list of [[Solution]]s in the GUI.
   *
   *  @param solutions the [[Solution]]s to display
   */
  def displaySolutions(solutions: List[Solution]): Unit

  /** Resets the display of [[Solution]]s. */
  def resetSolutions(): Unit
}

/** Factory for creating new instances of [[SolutionTab]]. */
object SolutionTab {
  import it.unibo.pcd.assignment2.eventdriven.controller.Controller
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component.AbstractComponent
  import it.unibo.pcd.assignment2.eventdriven.view.components.cards.SolutionCard
  import it.unibo.pcd.assignment2.eventdriven.AnyOps.discard
  import it.unibo.pcd.assignment2.eventdriven.view.components.LoadingLabel
  import javafx.fxml.FXML
  import javafx.scene.control._
  import scalafx.scene.layout.{VBox, Pane}

  import java.time.{LocalDateTime, LocalTime}

  /* Implementation of the SolutionTab Component. */
  private final class SolutionTabImpl(controller: Controller) extends AbstractComponent[Tab](fxmlFileName =  "solutionTab.fxml")
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
      val container: VBox = new VBox(spacing = 5)
      discard { container.children ++= solutions.map(SolutionCard(_)) }
      solutionsField.setContent(container)
    }

    override def resetSolutions(): Unit = {
      solutionsField.setContent(new Pane)
    }
  }

  /** Creates a new instance of [[SolutionTab]].
   *
   *  @param controller the [[Controller]] component used for the needed interactions with the backend of this application
   *  @return a new instance of [[SolutionTab]]
   */
  def apply(controller: Controller): SolutionTab = new SolutionTabImpl(controller)
}
