package de.christian2003.core.common.formatter

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject


/**
 * Service for formatting dates and times.
 */
class DateTimeFormatterService @Inject constructor() {

    /**
     * Formats the specified time.
     *
     * @param time  Time to format.
     * @return      Formatted time.
     */
    fun format(time: LocalDateTime): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        return time.format(formatter)
    }

}
