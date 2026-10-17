package com.s8125078.assignment2.data.remote.dto

import com.google.gson.annotations.SerializedName

// Entities are kept as maps because the fields change depending on the topic
// (history has event/startYear etc), so this works for any keypass.
data class DashboardResponse(
    @SerializedName("entities") val entities: List<Map<String, Any?>>?,
    @SerializedName("entityTotal") val entityTotal: Int?
)
