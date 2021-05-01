package it.unibo.pcd.assignment2.eventdriven

import it.unibo.pcd.assignment2.eventdriven.view.View
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage

/** Main class of the application. */
object Main extends JFXApp {
  stage = new PrimaryStage
  View(stage)
}
