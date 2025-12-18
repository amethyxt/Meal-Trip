package com.example.mealtrip.network

import com.google.gson.annotations.SerializedName

// -----------------------
// User / Auth
// -----------------------

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class UserResponse(
    @SerializedName("_id")
    val id: String,
    val username: String,
    val email: String,
    val password: String? = null,
    val preferences: List<String>? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val user: UserResponse?
)

// -----------------------
// Trip / Join / Start
// -----------------------

data class CreateTripRequest(
    @SerializedName("trip_name")
    val tripName: String,

    @SerializedName("host_id")
    val hostId: String,

    @SerializedName("budget_money")
    val budgetMoney: Int,

    @SerializedName("constraints")
    val constraints: Any? = null
)

data class TripResponse(
    @SerializedName("_id")
    val id: String,

    @SerializedName("trip_name")
    val tripName: String,

    @SerializedName("host_id")
    val hostId: String,

    @SerializedName("invite_code")
    val inviteCode: String? = null,

    @SerializedName("budget_money")
    val budgetMoney: Int? = null,

    @SerializedName("constraints")
    val constraints: Any? = null,

    @SerializedName("status")
    val status: String? = null
)

data class JoinTripRequest(
    val invite_code: String?,
    val user_id: String
)

data class JoinTripResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("trip_id")
    val tripId: String?
)

data class StartTripResponse(
    val message: String,
    val trip: TripResponse?
)

// -----------------------
// Voting
// -----------------------

data class VoteRequest(
    val trip_id: String,
    val user_id: String,
    val poi_id: String,
    val score: Int,
    val imageUrl: String? = null
)

data class VoteResponse(
    val message: String
)

// -----------------------
// Lobby
// -----------------------

data class MemberInfo(
    @SerializedName("user_id")
    val userId: String,
    val username: String,
    val email: String
)

data class GetMembersResponse(
    @SerializedName("members")
    val members: List<MemberInfo>,

    @SerializedName("trip_status")
    val tripStatus: String
)

// -----------------------
// Preferences
// -----------------------

data class PreferencesRequest(
    val preferences: List<String>
)

// -----------------------
// POI from Google API
// -----------------------

data class PoiItem(
    val poiId: String,
    val name: String,
    val type: String,
    val cost: Int,
    val time_min: Int,
    val lat: Double,
    val lng: Double,
    val imageUrl: String,
    val rating: Double
)

// -----------------------
// Trip Result
// -----------------------

data class TripPackageResponse(
    val tripId: String,
    val totalScore: Int,
    val totalCost: Int,
    val totalTimeMinutes: Int? = null,     // ⭐ เพิ่มฟิลด์นี้
    val tripPackage: List<PoiResult>?
)

data class PoiResult(
    val poiId: String,
    val name: String,
    val type: String,
    val score: Int,
    val cost: Int,
    val imageUrl: String? = null,

    val stayMinutes: Int? = null,
    val travelMinutesFromPrev: Int? = null,
    val startMinuteOffset: Int? = null,
    val endMinuteOffset: Int? = null
)
