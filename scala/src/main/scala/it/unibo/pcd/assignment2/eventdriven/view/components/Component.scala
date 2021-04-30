package it.unibo.pcd.assignment2.eventdriven.view.components

trait Component[A] {
    def inner: A
}

object Component {
  import javafx.fxml.FXMLLoader

  import scala.language.implicitConversions

  abstract class AbstractComponent[A](fxmlFileName: String) extends Component[A] {
    protected val loader = new FXMLLoader
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource(fxmlFileName))
  }

  implicit def componentToInner[A](component: Component[A]): A = component.inner
}
