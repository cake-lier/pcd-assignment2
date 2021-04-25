package it.unibo.pcd.assignment2.eventdriven.view.cards

import it.unibo.pcd.assignment2.eventdriven.model.Train
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.TitledPane
import org.apache.commons.text.WordUtils

sealed trait TrainCard {
  def titledPane: TitledPane
}

object TrainCard {
  private class TrainCardImpl(train: Train) extends TrainCard {
    @FXML
    private var root: TitledPane = _

    val loader = new FXMLLoader
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource("trainCard.fxml"))
    loader.load()
    root.setText(s"${WordUtils.capitalizeFully(train.trainType.toString.replace("_", " "))} ${train.trainCode.getOrElse("")}")

    override val titledPane: TitledPane = root
  }

  def apply(train: Train): TrainCard = new TrainCardImpl(train)
}