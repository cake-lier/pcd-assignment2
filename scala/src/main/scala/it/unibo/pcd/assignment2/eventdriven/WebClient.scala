package it.unibo.pcd.assignment2.eventdriven

import io.vertx.core.{Future, MultiMap}
import io.vertx.ext.web.client.HttpResponse

sealed trait WebClient {
  def get(host: String, requestURI: String): Future[String]

  def post(host: String, requestURI: String, body: Map[String, String]): Future[String]
}

object WebClient {
  import io.vertx.core.Vertx
  import io.vertx.ext.web.client.{WebClient => VertxWebClient}
  import io.vertx.core.buffer.Buffer
  import scala.language.implicitConversions
  import scala.jdk.CollectionConverters._

  private case class WebClientImpl(vertx: Vertx) extends WebClient {
    val webClient: VertxWebClient = VertxWebClient.create(vertx)

    override def get(host: String, requestURI: String): Future[String] =
      webClient.get(host, requestURI).send()

    override def post(host: String, requestURI: String, body: Map[String, String]): Future[String] =
      webClient.post(host, requestURI).sendForm(MultiMap.caseInsensitiveMultiMap().addAll(body.asJava))

    private implicit def responseToBody(req: Future[HttpResponse[Buffer]]): Future[String] =
      req.compose(res => Future.succeededFuture(res.bodyAsString))
  }

  def apply(vertx: Vertx): WebClient = WebClientImpl(vertx)
}
