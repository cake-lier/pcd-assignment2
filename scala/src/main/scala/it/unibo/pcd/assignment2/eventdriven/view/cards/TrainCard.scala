package it.unibo.pcd.assignment2.eventdriven.view.cards

import it.unibo.pcd.assignment2.eventdriven.model.SolutionTrain
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.{Label, TitledPane}
import javafx.scene.layout.VBox
import org.apache.commons.text.WordUtils

import java.time.format.DateTimeFormatter

object TrainCard {
  private class TrainCardImpl(train: SolutionTrain) extends Card[TitledPane] {
    @FXML
    private var root: TitledPane = _
    @FXML
    private var departureField: Label = _
    @FXML
    private var arrivalField: Label = _
    @FXML
    private var stopsField: VBox = _

    val loader = new FXMLLoader
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource("trainCard.fxml"))
    loader.load()
    root.setText(s"${WordUtils.capitalizeFully(train.trainType.toString.replace("_", " "))} ${train.trainCode.getOrElse("")}")
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle' HH:mm")
    departureField.setText(s"${departureField.getText}${train.departureStation.stationName} il " +
                           s"${train.departureStation.datetime.format(formatter)}")
    arrivalField.setText(s"${arrivalField.getText}${train.arrivalStation.stationName} il " +
                         s"${train.arrivalStation.datetime.format(formatter)}")
    stopsField.getChildren.setAll(train.stops.zipWithIndex.map(p => SolutionStopCard(p._1, p._2 + 1)).map(_.pane): _*)

    override val pane: TitledPane = root
  }

  def apply(train: SolutionTrain): Card[TitledPane] = new TrainCardImpl(train)
}