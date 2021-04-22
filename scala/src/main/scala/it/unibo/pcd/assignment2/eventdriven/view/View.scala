package it.unibo.pcd.assignment2.eventdriven.view

import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene

sealed trait View

object View {
  private class ViewImpl(primaryStage: PrimaryStage) extends View {
    primaryStage.title = "Trenitalia Information Center"
    primaryStage.centerOnScreen
    primaryStage.scene = new Scene(800, 600)
  }

  def apply(primaryStage: PrimaryStage): View = new ViewImpl(primaryStage)
}
