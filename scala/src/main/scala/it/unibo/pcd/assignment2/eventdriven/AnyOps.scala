package it.unibo.pcd.assignment2.eventdriven

import scala.language.implicitConversions

object AnyOps {
  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  implicit final class AnyOps[A](self: A) {
    def ===(other: A): Boolean = self == other

    def =/=(other: A): Boolean = ! ===(other)
  }

  @specialized
  def discard[A](eval: A): Unit = {
    val _: Any = eval
    ()
  }
}