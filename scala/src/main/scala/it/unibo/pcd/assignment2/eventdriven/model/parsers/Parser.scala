package it.unibo.pcd.assignment2.eventdriven.model.parsers

trait Parser[A] {
  def parse(json: String): A
}
