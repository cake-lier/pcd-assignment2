package it.unibo.pcd.assignment2.eventdriven.controller

import io.vertx.core.Future

/** A web client able to make HTTP requests.
 *
 *  It is able to make the two basic requests which are used while interacting with web pages: GET and POST. As it happens
 *  with web pages, when using the POST method in a HTTP request, the request itself can only has a form as its body.
 *  The instantiation is made through its companion object.
 */
sealed trait WebClient {
  /** Performs a GET HTTP request to the URL for which the host part and path part are given.
   *
   *  @param host the host part of the URL of the request
   *  @param path the path part of the URL of the request
   *  @return a [[Future]] representing the progressing request, which will contain the body of the response when completed
   */
  def get(host: String, path: String): Future[String]

  /** Performs a POST HTTP request to the URL for which the host part and path part are given.
   *
   *  @param host the host part of the URL of the request
   *  @param path the path part of the URL of the request
   *  @param body the body of the request, as a set of key-value pairs which are the form the body represents
   *  @return a [[Future]] representing the progressing request, which will contain the body of the response when completed
   */
  def post(host: String, path: String, body: Map[String, String]): Future[String]
}

/** Factory for [[WebClient]] instances. */
object WebClient {
  import io.vertx.core.{Vertx, MultiMap}
  import io.vertx.core.buffer.Buffer
  import io.vertx.ext.web.client.{WebClient => VertxWebClient, WebClientSession, HttpResponse}

  import scala.jdk.CollectionConverters._

  /* Leverages a Vertx VertxWebClient for making HTTP(S) requests. */
  private final case class WebClientImpl(vertx: Vertx) extends WebClient {
    private val webClient: WebClientSession = WebClientSession.create(VertxWebClient.create(vertx))
    private val httpsPort = 443

    /* Maps an HTTP response with a Buffer body to the body itself condensed into a String */
    private def fromResponseToBody(req: Future[HttpResponse[Buffer]]): Future[String] =
      req.compose(res => Future.succeededFuture(res.bodyAsString))

    override def get(host: String, path: String): Future[String] =
      fromResponseToBody(webClient.get(httpsPort, host, path).ssl(true).send())

    override def post(host: String, path: String, body: Map[String, String]): Future[String] =
      fromResponseToBody(webClient.post(host, path).sendForm(MultiMap.caseInsensitiveMultiMap().addAll(body.asJava)))
  }

  /** Creates a new instance of the [[WebClient]] trait.
   *
   *  By default, the GET requests are always performed over HTTPS, while the POST requests are always made over HTTP for
   *  complying with Trenitalia management of endpoints. For the same reason, all the requests are made over the same session,
   *  so this client is capable of managing cookies for session management.
   *  @param vertx the [[Vertx]] instance on which instantiating the web client
   *  @return a new instance of the [[WebClient]] trait
   */
  def apply(vertx: Vertx): WebClient = WebClientImpl(vertx)
}
