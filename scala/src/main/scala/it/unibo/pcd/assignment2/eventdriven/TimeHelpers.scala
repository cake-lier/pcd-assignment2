package it.unibo.pcd.assignment2.eventdriven

/** Collection of helper methods for managing time conversions. */
object TimeHelpers {
  import java.time.{Instant, LocalDateTime, ZoneId}
  import scala.language.implicitConversions

  /** Converts the milliseconds of a UNIX timestamp in a [[LocalDateTime]] object.  */
  implicit def millisToLocalDateTime(millis: Long): LocalDateTime =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault).toLocalDateTime
}
