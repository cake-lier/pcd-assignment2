package it.unibo.pcd.assignment2.eventdriven.view.components.cards

object TrainCard {
  import it.unibo.pcd.assignment2.eventdriven.model.SolutionTrain
  import it.unibo.pcd.assignment2.eventdriven.AnyOps.discard
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component.AbstractComponent
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component
  import javafx.fxml.FXML
  import javafx.scene.control.{Label, TitledPane}
  import javafx.scene.layout.VBox

  import java.time.format.DateTimeFormatter

  private class TrainCardImpl(train: SolutionTrain) extends AbstractComponent[TitledPane]("trainCard.fxml") {
    @FXML
    private var departureField: Label = new Label
    @FXML
    private var arrivalField: Label = new Label
    @FXML
    private var stopsField: VBox = new VBox

    override val inner: TitledPane = loader.load[TitledPane]
    inner.setText(s"${train.trainType.toString} ${train.trainCode.getOrElse("")}")
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle' HH:mm")
    departureField.setText(s"${departureField.getText}${train.departureStation.stationName} il " +
                           s"${train.departureStation.datetime.format(formatter)}")
    arrivalField.setText(s"${arrivalField.getText}${train.arrivalStation.stationName} il " +
                         s"${train.arrivalStation.datetime.format(formatter)}")
    discard { stopsField.getChildren.setAll(train.stops.zipWithIndex.map(p => SolutionStopCard(p._1, p._2 + 1).inner): _*) }
  }

  def apply(train: SolutionTrain): Component[TitledPane] = new TrainCardImpl(train)
}