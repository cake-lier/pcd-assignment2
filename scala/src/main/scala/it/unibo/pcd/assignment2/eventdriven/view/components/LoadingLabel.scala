package it.unibo.pcd.assignment2.eventdriven.view.components

/** Factory for creating labels used in loading screens. */
object LoadingLabel {
  import scalafx.geometry.Pos
  import scalafx.scene.control.Label

  /* Implementation of a loading label. */
  private final class LoadingLabelImpl extends Component[Label] {
    override val inner = new Label(text = "Caricamento in corso")

    inner.maxWidth = Double.MaxValue
    inner.maxHeight = Double.MaxValue
    inner.alignment = Pos.Center
    val css = "-fx-font-size: 20"
    inner.setStyle(css)
  }

  /** Returns a new instance of a loading label. */
  def apply(): Component[Label] = new LoadingLabelImpl
}
