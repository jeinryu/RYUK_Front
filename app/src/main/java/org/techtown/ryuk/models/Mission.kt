package org.techtown.ryuk.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class JsonGetMissions(
    @SerializedName("status") val status: String,
    @SerializedName("data") val mission: List<Mission>
)
data class JsonAddMission(
    @SerializedName("status") val status: String,
    @SerializedName("data") val missionId: MissionId
)
data class JsonAssignMission(
    @SerializedName("status") val status: String,
    @SerializedName("data") val mission: MissionAssign
)
data class JsonBoolean(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: Boolean
)
data class Mission(
    @SerializedName("id") val id: Int,
    @SerializedName("is_success") var is_success: Int,
    @SerializedName("title") val title: String,
    @SerializedName("mission_type") val mission_type: String,
    @SerializedName("submit_type") val submit_type: String,
)
data class MissionAssign(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val user_id: Int,
    @SerializedName("mission_id") val mission_id: Int,
    @SerializedName("is_success") val is_success: Int,
    @SerializedName("mission_date") val mission_date: String,
)
data class MissionId(
    @SerializedName("id") val id: Int
)
