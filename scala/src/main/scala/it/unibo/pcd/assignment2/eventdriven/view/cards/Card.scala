package it.unibo.pcd.assignment2.eventdriven.view.cards

import javafx.scene.layout.Region

trait Card[A <: Region] {
    def pane: A
}
