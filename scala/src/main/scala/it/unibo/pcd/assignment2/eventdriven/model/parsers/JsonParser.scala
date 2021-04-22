package it.unibo.pcd.assignment2.eventdriven.model.parsers

trait JsonParser[A] {
  def parse(json: String): A
}
