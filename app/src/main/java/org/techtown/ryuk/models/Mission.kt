package org.techtown.ryuk.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Json(
    @SerializedName("status") val status: String,
    @SerializedName("data") val mission: Mission
)
data class Mission(
    @SerializedName("User_Mission_id") val id: Int,
    @SerializedName("date") val date: String,
    @SerializedName("title") val title: String,
    @SerializedName("type") val type: List<Int>,
    @SerializedName("team") val team: List<Int>
)
