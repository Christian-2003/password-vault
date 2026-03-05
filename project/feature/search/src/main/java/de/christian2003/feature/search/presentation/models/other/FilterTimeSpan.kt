package de.christian2003.feature.search.presentation.models.other

import java.time.LocalDate


/**
 * Time span for the filters on the search screen.
 * Either start or end can be null. In such a case, the time span is includes everything before the
 * end day (if start is null) or everything after the start day (if end is null).
 * If both start and end are null, the time span includes everything.
 *
 * @param start Start day of the time span. If this is null, start day is undefined.
 * @param end   End day of the time span. If this is null, end day is undefined.
 */
data class FilterTimeSpan(
    val start: LocalDate?,
    val end: LocalDate?
) {

    companion object {

        /**
         * Default time span for today.
         */
        val Today = FilterTimeSpan(
            start = LocalDate.now(),
            end = LocalDate.now()
        )

        /**
         * Default time span for the last week.
         */
        val LastWeek = FilterTimeSpan(
            start = LocalDate.now().minusDays(7),
            end = LocalDate.now()
        )

        /**
         * Default time span for no time span.
         */
        val All = FilterTimeSpan(
            start = null,
            end = null
        )

    }

}
