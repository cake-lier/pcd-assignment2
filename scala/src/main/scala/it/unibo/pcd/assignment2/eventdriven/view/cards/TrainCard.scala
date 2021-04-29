package it.unibo.pcd.assignment2.eventdriven.view.cards

import it.unibo.pcd.assignment2.eventdriven.model.SolutionTrain
import it.unibo.pcd.assignment2.eventdriven.AnyOps.discard
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.{Label, TitledPane}
import javafx.scene.layout.VBox

import java.time.format.DateTimeFormatter

object TrainCard {
  private class TrainCardImpl(train: SolutionTrain) extends Card[TitledPane] {
    @FXML
    private var departureField: Label = new Label
    @FXML
    private var arrivalField: Label = new Label
    @FXML
    private var stopsField: VBox = new VBox

    val loader = new FXMLLoader
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource("trainCard.fxml"))
    override val pane: TitledPane = loader.load[TitledPane]
    pane.setText(s"${train.trainType.toString} ${train.trainCode.getOrElse("")}")
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle' HH:mm")
    departureField.setText(s"${departureField.getText}${train.departureStation.stationName} il " +
                           s"${train.departureStation.datetime.format(formatter)}")
    arrivalField.setText(s"${arrivalField.getText}${train.arrivalStation.stationName} il " +
                         s"${train.arrivalStation.datetime.format(formatter)}")
    discard { stopsField.getChildren.setAll(train.stops.zipWithIndex.map(p => SolutionStopCard(p._1, p._2 + 1)).map(_.pane): _*) }
  }

  def apply(train: SolutionTrain): Card[TitledPane] = new TrainCardImpl(train)
}