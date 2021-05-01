package it.unibo.pcd.assignment2.eventdriven.view.components.cards

import it.unibo.pcd.assignment2.eventdriven.model.TravelStateEnum.{Delayed, Early, InTime}

/** A card for displaying the information about a [[it.unibo.pcd.assignment2.eventdriven.model.TrainBoardRecord]]. */
object TrainBoardCard {
  /** The type of train board to which this card belongs to, the arrival train board or the departure one. */
  sealed trait Type {
    /** Returns the specific message to display for this card. */
    def message: String
  }

  /** Collects all possible types of train board cards. */
  object Type {
    /** The type of the departure train board cards. */
    final case object Departure extends Type {
      override val message = "in partenza per"
    }

    /** The type of the arrival train board cards. */
    final case object Arrival extends Type {
      override val message = "in arrivo da"
    }
  }

  import it.unibo.pcd.assignment2.eventdriven.model.TrainBoardRecord
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component
  import it.unibo.pcd.assignment2.eventdriven.view.components.Component.AbstractComponent
  import javafx.fxml.FXML
  import javafx.scene.control.Label
  import javafx.scene.layout.{GridPane, Pane}

  import java.time.format.DateTimeFormatter

  /* Implementation of a card for displaying a TrainBoardRecord. */
  private class TrainBoardCardImpl(trainBoardRecord: TrainBoardRecord, cardType: Type)
    extends AbstractComponent[Pane](fxmlFileName = "trainBoardCard.fxml") {
    @FXML
    private var trainBoardTitle: Label = new Label
    @FXML
    private var trainBoardDelay: Label = new Label
    @FXML
    private var trainBoardPlannedPlatform: Label = new Label
    @FXML
    private var trainBoardExpectedPlatform: Label = new Label

    override val inner: Pane = loader.load[GridPane]
    val timePattern = "HH:mm"
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(timePattern)
    trainBoardTitle.setText(s"${trainBoardRecord.train.trainType.toString} " +
                            s"${trainBoardRecord.train.trainCode.getOrElse("")} " +
                            s"delle ore ${trainBoardRecord.time.format(formatter)} " +
                            cardType.message +
                            s" ${trainBoardRecord.station.stationName}")
    trainBoardDelay.setText(trainBoardRecord.state.state match {
      case Delayed => s"In ritardo di ${trainBoardRecord.state.delay.getOrElse(0).toString} minuti"
      case Early => s"In anticipo di ${(-trainBoardRecord.state.delay.getOrElse(0)).toString} minuti"
      case InTime => "In orario"
      case _ => "--"
    })
    trainBoardPlannedPlatform.setText(s"Binario programmato: ${trainBoardRecord.expectedPlatform.getOrElse("--")}")
    trainBoardExpectedPlatform.setText(s"Binario effettivo: ${trainBoardRecord.actualPlatform.getOrElse("--")}")
  }

  /** Creates a new instance of a [[Component]] for displaying a [[TrainBoardRecord]].
   *
   *  @param trainBoardRecord the [[TrainBoardRecord]] which information is to display
   *  @param cardType the type of train board to which this card [[Component]] belongs to
   *  @return a new card [[Component]] for displaying a [[TrainBoardRecord]]
   */
  def apply(trainBoardRecord: TrainBoardRecord, cardType: Type): Component[Pane] =
    new TrainBoardCardImpl(trainBoardRecord, cardType)
}
