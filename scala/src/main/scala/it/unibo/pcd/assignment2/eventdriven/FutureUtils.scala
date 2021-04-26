package it.unibo.pcd.assignment2.eventdriven

import io.vertx.core.{CompositeFuture, Future}
import scala.jdk.CollectionConverters._

import java.util

object FutureUtils {
  def all[A](futures: List[Future[A]]): Future[List[A]] =
      CompositeFuture.all(futures.asJava.asInstanceOf[util.List[Future[_]]])
                     .compose(_ => Future.succeededFuture(futures.map(_.result)))
}
