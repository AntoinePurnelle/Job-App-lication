package eu.antoinepurnelle.jobapplication.data.transformer

import kotlinx.datetime.LocalDate

object LocalDateParser {
    fun parse(date: String?): LocalDate? = date?.let { dateStr -> runCatching { LocalDate.Companion.parse(dateStr) }.getOrNull() }
}
