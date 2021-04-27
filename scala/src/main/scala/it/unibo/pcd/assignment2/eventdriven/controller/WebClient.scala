package it.unibo.pcd.assignment2.eventdriven.controller

import io.vertx.core.{Future, MultiMap}
import io.vertx.ext.web.client.{HttpResponse, WebClientSession}

sealed trait WebClient {
  def get(host: String, requestURI: String): Future[String]

  def post(host: String, requestURI: String, body: Map[String, String]): Future[String]
}

object WebClient {
  import io.vertx.core.Vertx
  import io.vertx.core.buffer.Buffer
  import io.vertx.ext.web.client.{WebClient => VertxWebClient}

  import scala.jdk.CollectionConverters._

  private case class WebClientImpl(vertx: Vertx) extends WebClient {
    val webClient: WebClientSession = WebClientSession.create(VertxWebClient.create(vertx))
    val httpsPort = 443

    override def get(host: String, requestURI: String): Future[String] =
      fromResponseToBody(webClient.get(httpsPort, host, requestURI).ssl(true).send())

    override def post(host: String, requestURI: String, body: Map[String, String]): Future[String] =
      fromResponseToBody(webClient.post(host, requestURI)
                                  .sendForm(MultiMap.caseInsensitiveMultiMap().addAll(body.asJava)))

    private def fromResponseToBody(req: Future[HttpResponse[Buffer]]): Future[String] =
      req.compose(res => Future.succeededFuture(res.bodyAsString))
  }

  def apply(vertx: Vertx): WebClient = WebClientImpl(vertx)
}
