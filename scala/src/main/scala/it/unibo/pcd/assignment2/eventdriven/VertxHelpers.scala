package it.unibo.pcd.assignment2.eventdriven

/** Collects helper methods for allowing a simpler use of the Vertx library with the Scala language. */
object VertxHelpers {
  import io.vertx.core.Handler
  import scala.language.implicitConversions

  /** Converts a Scala [[Function1]] with Unit return type to a Vertx [[Handler]]. */
  implicit def fromFunctionToHandler[A](f: A => Unit): Handler[A] = f.apply
}
