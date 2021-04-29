package it.unibo.pcd.assignment2.eventdriven.view

import scalafx.geometry.Pos
import scalafx.scene.control.Label

sealed trait LoadingLabel {
  def label: Label
}

object LoadingLabel {
  private final class LoadingLabelImpl extends LoadingLabel {
    override val label = new Label("Caricamento in corso")

    label.maxWidth = Double.MaxValue
    label.maxHeight = Double.MaxValue
    label.alignment = Pos.Center
    label.setStyle("-fx-font-size: 20")
  }

  def apply(): LoadingLabel = new LoadingLabelImpl
}
