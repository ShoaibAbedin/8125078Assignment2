package com.s8125078.assignment2.data.mapper

import com.s8125078.assignment2.data.model.Entity
import com.s8125078.assignment2.data.model.EntityField
import javax.inject.Inject

// Turns the raw entity maps from the API into Entity objects.
// Works for any topic: known fields (like startYear/endYear) get nicer formatting,
// anything else is shown as a "Label: value" field.
class EntityMapper @Inject constructor() {

    fun mapAll(rawEntities: List<Map<String, Any?>>): List<Entity> =
        rawEntities.mapIndexed { index, raw -> map(raw, index) }

    fun map(raw: Map<String, Any?>, index: Int): Entity {
        val descriptionKey = raw.keys.firstOrNull { it.equals(DESCRIPTION_KEY, ignoreCase = true) }
        val titleKey = findTitleKey(raw, descriptionKey)

        val startYear = raw[START_YEAR_KEY].toYear()
        val endYear = raw[END_YEAR_KEY].toYear()
        val hasPeriod = startYear != null && endYear != null

        val usedKeys = buildSet {
            descriptionKey?.let { add(it) }
            titleKey?.let { add(it) }
            if (hasPeriod) {
                add(START_YEAR_KEY)
                add(END_YEAR_KEY)
            }
        }

        val fields = raw
            .filterKeys { it !in usedKeys }
            .mapNotNull { (key, value) ->
                val text = formatValue(value)
                if (text.isNullOrBlank()) null else EntityField(formatLabel(key), text)
            }

        return Entity(
            title = titleKey?.let { formatValue(raw[it]) } ?: "Item ${index + 1}",
            period = if (hasPeriod) formatPeriod(startYear!!, endYear!!) else null,
            durationYears = if (hasPeriod && endYear!! >= startYear!!) endYear - startYear else null,
            fields = fields,
            description = descriptionKey?.let { formatValue(raw[it]) }?.takeIf { it.isNotBlank() }
        )
    }

    // prefers a known name field, otherwise the first text field
    private fun findTitleKey(raw: Map<String, Any?>, descriptionKey: String?): String? {
        val candidates = raw.filterKeys { it != descriptionKey }
        val known = TITLE_KEYS.firstNotNullOfOrNull { wanted ->
            candidates.keys.firstOrNull { it.equals(wanted, ignoreCase = true) && !formatValue(candidates[it]).isNullOrBlank() }
        }
        return known ?: candidates.entries.firstOrNull { (_, value) -> value is String && value.isNotBlank() }?.key
    }

    private fun formatPeriod(start: Int, end: Int): String =
        if (start == end) start.toString() else "$start – $end"

    // keyFigure / key_figure -> "Key figure"
    fun formatLabel(key: String): String {
        val words = key
            .replace('_', ' ')
            .replace(Regex("([a-z0-9])([A-Z])"), "$1 $2")
            .trim()
            .lowercase()
        return words.replaceFirstChar { it.uppercase() }
    }

    // Gson reads every JSON number as a Double, so 1939 arrives as 1939.0
    fun formatValue(value: Any?): String? = when (value) {
        null -> null
        is String -> value.trim()
        is Double -> if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
        is Float -> if (value % 1f == 0f) value.toLong().toString() else value.toString()
        is Number -> value.toString()
        is Boolean -> if (value) "Yes" else "No"
        is List<*> -> value.mapNotNull { formatValue(it) }.joinToString(", ")
        is Map<*, *> -> value.entries.joinToString(", ") { "${it.key}: ${formatValue(it.value)}" }
        else -> value.toString()
    }

    private fun Any?.toYear(): Int? = when (this) {
        is Number -> toInt()
        is String -> trim().toIntOrNull()
        else -> null
    }

    companion object {
        private const val DESCRIPTION_KEY = "description"
        private const val START_YEAR_KEY = "startYear"
        private const val END_YEAR_KEY = "endYear"
        private val TITLE_KEYS = listOf("event", "name", "title")
    }
}
