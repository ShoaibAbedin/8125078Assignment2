package com.s8125078.assignment2.data.model

import java.io.Serializable

// One item from the dashboard, already formatted for display.
// Serializable so it can be passed to the details screen with Safe Args.
data class Entity(
    val title: String,
    val period: String?, // e.g. "1939 – 1945", null if the topic has no years
    val durationYears: Int?,
    val fields: List<EntityField>, // everything except the title, years and description
    val description: String?
) : Serializable {

    // shown on the dashboard card - the spec says no description here
    val summary: String
        get() = fields.joinToString(" · ") { it.value }
}

data class EntityField(
    val label: String,
    val value: String
) : Serializable
