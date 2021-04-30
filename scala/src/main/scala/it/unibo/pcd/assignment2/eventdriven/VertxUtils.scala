package it.unibo.pcd.assignment2.eventdriven

import io.vertx.core.Handler
import scala.language.implicitConversions

object VertxUtils {
  implicit def fromFunctionToHandler[A](f: A => Unit): Handler[A] = f.apply
}
