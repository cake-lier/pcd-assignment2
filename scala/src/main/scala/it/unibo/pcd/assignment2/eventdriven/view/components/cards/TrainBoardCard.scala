package it.unibo.pcd.assignment2.eventdriven.view.components.cards

import javafx.scene.layout.GridPane

object TrainBoardCard {
  sealed trait Type
  object Type {
    final case object Departure extends Type
    final case object Arrival extends Type
  }

  import it.unibo.pcd.assignment2.eventdriven.AnyOps.AnyOps
  import it.unibo.pcd.assignment2.eventdriven.model.{TrainBoardRecord, TravelStateEnum}
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component.AbstractComponent
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component
  import javafx.scene.layout.Pane
  import javafx.scene.control.Label
  import javafx.fxml.FXML

  import java.time.format.DateTimeFormatter

  private class TrainBoardCardImpl(trainBoardRecord: TrainBoardRecord, cardType: Type)
    extends AbstractComponent[Pane]("trainBoardCard.fxml") {
    @FXML
    private var trainBoardTitle: Label = new Label
    @FXML
    private var trainBoardDelay: Label = new Label
    @FXML
    private var trainBoardPlannedPlatform: Label = new Label
    @FXML
    private var trainBoardExpectedPlatform: Label = new Label

    override val inner: Pane = loader.load[GridPane]
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

  def apply(trainBoardRecord: TrainBoardRecord, cardType: Type): Component[Pane] =
    new TrainBoardCardImpl(trainBoardRecord, cardType)
}
