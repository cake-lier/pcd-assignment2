package it.unibo.pcd.assignment2.eventdriven

import java.time.{Instant, LocalDateTime, ZoneId}
import scala.language.implicitConversions

object TimeUtils {
  implicit def fromMillisToLocalDateTime(millis: Long): LocalDateTime =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault).toLocalDateTime
}
