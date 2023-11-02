package org.techtown.ryuk.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class JsonGetMissions(
    @SerializedName("status") val status: String,
    @SerializedName("data") val mission: List<Mission>
)
data class JsonAddMission(
    @SerializedName("status") val status: String,
    @SerializedName("data") val mission: MissionAdd
)
data class JsonBoolean(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: Boolean
)
data class Mission(
    @SerializedName("user_mission_id") val user_mission_id: Int,
    @SerializedName("is_success") var is_success: Int,
    @SerializedName("title") val title: String,
    @SerializedName("mission_type") val mission_type: String,
    @SerializedName("submit_type") val submit_type: String,
    @SerializedName("from_team") val from_team: Int
)
data class MissionAdd(
    @SerializedName("user_mission_id") val user_mission_id: Int,
    @SerializedName("user_id") val user_id: Int,
    @SerializedName("mission_id") val mission_id: Int,
    @SerializedName("from_team") val from_team: Int,
    @SerializedName("is_success") val is_success: Int,
    @SerializedName("mission_date") val mission_date: String,
)