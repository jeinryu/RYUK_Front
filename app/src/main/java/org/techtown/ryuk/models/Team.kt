package org.techtown.ryuk.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class JsonGetTeams(
    @SerializedName("status") val status: String,
    @SerializedName("data") val teams: List<Team>
)

data class Team(
    @SerializedName("team_id") val teamId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("link") val link: String,
    @SerializedName("category") val category: String,
    @SerializedName("introduce") val introduce: String,
    @SerializedName("master_id") val masterId: Int,
    @SerializedName("start_day") val startDay: String,
    @SerializedName("end_day") val endDay: String
)

data class JsonTeamJoinResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: TeamJoinResult
)

data class TeamJoinResult(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("team_id") val teamId: Int
)

data class TeamMemberCount(
    @SerializedName("team_id") val teamId: Int,
    @SerializedName("team_member_num") val teamMemberNum: Int
)

data class JsonTeamMemberCountResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: List<TeamMemberCount>
)

data class TeamCheckResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: TeamId
)

data class TeamId(
    @SerializedName("team_id") val teamId: Int
)

data class JsonTeamDetailResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: Team
)