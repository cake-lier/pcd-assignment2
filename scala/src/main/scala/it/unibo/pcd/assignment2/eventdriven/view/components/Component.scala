package it.unibo.pcd.assignment2.eventdriven.view.components

/** A View component, which allows to wrap another component.
 *
 *  This trait allows to create modular View components which can be re-instantiated multiple times without
 *  specifying how to initialize them, because they encapsulate all the logic which is needed to create and style them.
 *  The instantiation is made through its companion object.
 *  @tparam A the type of component wrapped
 */
trait Component[A] {
  /** Returns the inner JavaFX component wrapped by this [[Component]]. */
  def inner: A
}

/** Factory for new [[Component]] instances. */
object Component {
  import javafx.fxml.FXMLLoader

  import scala.language.implicitConversions

  /** Represents a wrapper for JavaFX components which are instantiated through FXML files.
   *
   *  The FXMLLoader is set but does not actually load the file, so as to allow subclasses to bind their fields to the created
   *  objects. In order to do this, the FXMLLoader is assigned to a protected property.
   *  @constructor a new instance is created by specifying the name of the FXML file from which instantiating the [[Component]]
   *  @param fxmlFileName the name of the FXML file from which instantiating the [[Component]]
   *  @tparam A the type of JavaFX component wrapped
   */
  abstract class AbstractComponent[A](fxmlFileName: String) extends Component[A] {
    protected val loader = new FXMLLoader
    loader.setController(this)
    loader.setLocation(ClassLoader.getSystemResource(fxmlFileName))
  }

  /** Converts a component into its wrapped object through the [[Component.inner]] property.
   *
   *  @param component the [[Component]] to transform
   *  @tparam A the type of the wrapped component
   *  @return the wrapped component
   */
  implicit def componentToInner[A, B <: A](component: Component[B]): A = component.inner
}
