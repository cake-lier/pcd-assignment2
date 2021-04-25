package it.unibo.pcd.assignment2.eventdriven.view.cards

import it.unibo.pcd.assignment2.eventdriven.model.{TrainBoardRecord, TravelStateEnum}
import javafx.scene.layout.Pane
import org.apache.commons.text.WordUtils

import java.time.format.DateTimeFormatter

object TrainBoardCard {
  import javafx.fxml.{FXML, FXMLLoader}
  import javafx.scene.control.Label
  import javafx.scene.layout.GridPane

  object Type extends Enumeration {
    type Type = Value
    val DEPARTURE, ARRIVAL = Value
  }

  private class TrainBoardCardImpl(trainBoardRecord: TrainBoardRecord, cardType: Type.Value) extends Card[Pane] {
    @FXML
    private var root: GridPane = _
    @FXML
    private var trainBoardTitle: Label = _
    @FXML
    private var trainBoardDelay: Label = _
    @FXML
    private var trainBoardPlannedPlatform: Label = _
    @FXML
    private var trainBoardExpectedPlatform: Label = _

    val loader = new FXMLLoader
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource("trainBoardCard.fxml"))
    loader.load
    val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    trainBoardTitle.setText(s"${WordUtils.capitalizeFully(trainBoardRecord.train.trainType.toString.replace("_", " "))} " +
                            s"${trainBoardRecord.train.trainCode.getOrElse("")} " +
                            s"delle ore ${trainBoardRecord.time.format(timeFormatter)} " +
                            (if (cardType == Type.DEPARTURE) "in partenza per" else "in arrivo da") +
                            s" ${trainBoardRecord.station.stationName}")
    trainBoardDelay.setText(
      if (trainBoardRecord.state.state == TravelStateEnum.DELAYED) {
        s"In ritardo di ${trainBoardRecord.state.delay.get} minuti"
      } else if (trainBoardRecord.state.state == TravelStateEnum.EARLY) {
        s"In anticipo di ${-trainBoardRecord.state.delay.get} minuti"
      } else {
        "In orario"
      }
    )
    trainBoardPlannedPlatform.setText(s"Binario programmato: ${trainBoardRecord.expectedPlatform.getOrElse("--")}")
    trainBoardExpectedPlatform.setText(s"Binario effettivo: ${trainBoardRecord.actualPlatform.getOrElse("--")}")

    override val pane: Pane = root
  }

  def apply(trainBoardRecord: TrainBoardRecord, cardType: Type.Value): Card[Pane] =
    new TrainBoardCardImpl(trainBoardRecord, cardType)
}
