package it.unibo.pcd.assignment2.eventdriven.view.components

import scalafx.geometry.Pos
import scalafx.scene.control.Label

object LoadingLabel {
  private final class LoadingLabelImpl extends Component[Label] {
    override val inner = new Label("Caricamento in corso")

    inner.maxWidth = Double.MaxValue
    inner.maxHeight = Double.MaxValue
    inner.alignment = Pos.Center
    inner.setStyle("-fx-font-size: 20")
  }

  def apply(): Component[Label] = new LoadingLabelImpl
}
