package org.techtown.ryuk.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val userData: User?
)

data class LoginRequest(
    @SerializedName("login_id") val loginId: String,
    @SerializedName("password") val password: String
)

data class User(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("login_id") val loginId: String,
    @SerializedName("password") val password: String,
    @SerializedName("user_name") val userName: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("email") val email: String,
    @SerializedName("is_manager") val isManager: Int,
    @SerializedName("team_user_id") val teamUserId : Int?,
    @SerializedName("team_id") val teamId: Int?
)

data class UserResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val userData: User?
)

data class TeamMembersResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val members: List<User>
)

data class TeamWithdrawResponse(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("team_id") val teamId: Int
)