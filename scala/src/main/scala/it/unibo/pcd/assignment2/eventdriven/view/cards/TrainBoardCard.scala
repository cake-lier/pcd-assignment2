package it.unibo.pcd.assignment2.eventdriven.view.cards

import it.unibo.pcd.assignment2.eventdriven.AnyOps.AnyOps
import it.unibo.pcd.assignment2.eventdriven.model.{TrainBoardRecord, TravelStateEnum}
import javafx.scene.layout.Pane

import java.time.format.DateTimeFormatter

object TrainBoardCard {
  import javafx.fxml.{FXML, FXMLLoader}
  import javafx.scene.control.Label
  import javafx.scene.layout.GridPane

  sealed trait Type
  object Type {
    case object Departure extends Type
    case object Arrival extends Type
  }

  private class TrainBoardCardImpl(trainBoardRecord: TrainBoardRecord, cardType: Type) extends Card[Pane] {
    @FXML
    private var trainBoardTitle: Label = new Label
    @FXML
    private var trainBoardDelay: Label = new Label
    @FXML
    private var trainBoardPlannedPlatform: Label = new Label
    @FXML
    private var trainBoardExpectedPlatform: Label = new Label

    val loader = new FXMLLoader
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource("trainBoardCard.fxml"))
    override val pane: Pane = loader.load[GridPane]
    val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    trainBoardTitle.setText(s"${trainBoardRecord.train.trainType.toString} " +
                            s"${trainBoardRecord.train.trainCode.getOrElse("")} " +
                            s"delle ore ${trainBoardRecord.time.format(timeFormatter)} " +
                            (if (cardType === Type.Departure) "in partenza per" else "in arrivo da") +
                            s" ${trainBoardRecord.station.stationName}")
    trainBoardDelay.setText(
      if (trainBoardRecord.state.state === TravelStateEnum.Delayed) {
        s"In ritardo di ${trainBoardRecord.state.delay.getOrElse(0).toString} minuti"
      } else if (trainBoardRecord.state.state === TravelStateEnum.Early) {
        s"In anticipo di ${(-trainBoardRecord.state.delay.getOrElse(0)).toString} minuti"
      } else {
        "In orario"
      }
    )
    trainBoardPlannedPlatform.setText(s"Binario programmato: ${trainBoardRecord.expectedPlatform.getOrElse("--")}")
    trainBoardExpectedPlatform.setText(s"Binario effettivo: ${trainBoardRecord.actualPlatform.getOrElse("--")}")
  }

  def apply(trainBoardRecord: TrainBoardRecord, cardType: Type): Card[Pane] =
    new TrainBoardCardImpl(trainBoardRecord, cardType)
}
