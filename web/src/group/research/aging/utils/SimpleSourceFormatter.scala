package group.research.aging.utils

import wvlet.log.LogFormatter.{appendStackTrace, highlightLog, withColor}
import wvlet.log.{LogFormatter, LogRecord}
import wvlet.log.LogTimestampFormatter.formatTimestamp


object SimpleSourceFormatter extends LogFormatter {
  override def formatLog(r: LogRecord): String = {
    val loc =
      r.source
        .map(source => s" - (${source.fileLoc})")
        .getOrElse("")

    val logTag = r.level.name
    val log =
      f"${formatTimestamp(r.getMillis)} ${logTag} [${r.leafLoggerName}] ${r.getMessage} ${loc}"
    appendStackTrace(log, r)
  }
}
