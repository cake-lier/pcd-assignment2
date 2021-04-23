package it.unibo.pcd.assignment2.eventdriven.view

import it.unibo.pcd.assignment2.eventdriven.controller.Controller
import javafx.application.Platform
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.layout.FlowPane

import java.time.LocalDateTime

sealed trait View

object View {
  private class ViewImpl(primaryStage: PrimaryStage) extends View {
    val controller: Controller = Controller()

    primaryStage.title = "Trenitalia Information Center"
    primaryStage.centerOnScreen
    val scene = new Scene(800, 600)
    primaryStage.scene = scene
    val root = new FlowPane()
    controller.getTrainSolutions("ANGUILLARA", "ABANO", LocalDateTime.now())
              .onSuccess(l => Platform.runLater(() => {
                  root.children = new Label(l.toString)
              }))
              .onFailure(_.printStackTrace())
    scene.root = root
  }

  def apply(primaryStage: PrimaryStage): View = new ViewImpl(primaryStage)
}
