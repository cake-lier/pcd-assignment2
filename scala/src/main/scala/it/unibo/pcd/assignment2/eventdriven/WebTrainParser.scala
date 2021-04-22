package it.unibo.pcd.assignment2.eventdriven

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

object WebTrainParser extends App {

  val browser = JsoupBrowser()
  val doc2 = browser.get("http://www.viaggiatreno.it/vt_pax_internet/mobile/scheda?numeroTreno=3902")
  val trainId = doc2 >> text("h1")
  val elements = doc2 >> elementList(".corpocentrale")
  //println(elements map(e=> (e>>text("h2")) ->(e>>text("p"))) toMap)
  println(elements >> allText("h2"))
  //println(doc2 >> elementList("div") map (_>>allText))
}
