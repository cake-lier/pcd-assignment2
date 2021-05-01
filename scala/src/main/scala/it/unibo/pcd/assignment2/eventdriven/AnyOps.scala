package it.unibo.pcd.assignment2.eventdriven

/** Collection of extension utilities for the Any trait. */
object AnyOps {
  import scala.language.implicitConversions

  /** Defines the "equals" and "not equals" operations without using cooperative equality.  */
  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  implicit final class AnyOps[A](self: A) {
    /** Checks if two objects are equal without using cooperative equality. */
    def ===(other: A): Boolean = self == other

    /** Checks if two objects are not equal without using cooperative equality. */
    def =/=(other: A): Boolean = self != other
  }

  /** Allows to explicitly discard a result of a method making it a Unit returning statement. */
  @specialized
  def discard[A](eval: A): Unit = {
    val _: Any = eval
    ()
  }
}
