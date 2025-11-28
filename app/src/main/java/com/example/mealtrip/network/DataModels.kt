package com.example.mealtrip.network

import com.google.gson.annotations.SerializedName

// --- User Models ---
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class UserResponse(
    @SerializedName("_id") val id: String,
    val username: String,
    val email: String,
    @SerializedName("createdAt") val createdAt: String,
    val preferences: List<String>? = null
)

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val message: String, val user: UserResponse)

// --- Trip Models ---
data class CreateTripRequest(
    @SerializedName("trip_name") val tripName: String,
    @SerializedName("host_id") val hostId: String,
    @SerializedName("budget_money") val budgetMoney: Int,
    val constraints: Map<String, List<Map<String, Any>>>
)

data class TripResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("trip_name") val tripName: String,
    @SerializedName("invite_code") val inviteCode: String
)

data class JoinTripRequest(
    @SerializedName("invite_code") val inviteCode: String,
    @SerializedName("user_id") val userId: String
)

data class JoinTripResponse(
    val message: String,
    @SerializedName("trip_id") val tripId: String
)

// --- Vote Models ---
data class VoteRequest(
    @SerializedName("trip_id") val tripId: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("poi_id") val poiId: String,
    val score: Int
)

data class VoteResponse(val message: String)

// --- Result Models ---
data class TripPackageResponse(
    val message: String,
    @SerializedName("package") val tripPackage: List<PoiResult>,
    val remainingBudget: Int,
    val totalScore: Int
)

data class PoiResult(
    @SerializedName("poi_id") val poiId: String,
    val name: String,
    val cost: Int,
    val type: String,
    val score: Int,
    val lat: Double = 0.0,
    val lng: Double = 0.0
)

// --- POI Models (สำหรับดึงข้อมูลจริงจาก Google) ---
data class PoiItem(
    val poiId: String,
    val name: String,
    val type: String,
    val cost: Int,
    val imageUrl: String,

    // ▼▼▼ เพิ่มบรรทัดนี้ครับ! (ใช้จำค่าคะแนนดาวที่เรากดไป) ▼▼▼
    var myScore: Int = 0
)

// --- Preferences ---
data class PreferencesRequest(val preferences: List<String>)

// --- Lobby ---
data class MemberInfo(
    @SerializedName("user_id") val userId: String,
    val username: String,
    val email: String
)

data class GetMembersResponse(
    val members: List<MemberInfo>,
    @SerializedName("trip_status") val tripStatus: String
)

data class StartTripResponse(val message: String)

// Mock Data
object MockPoiDataSource {
    val poiList = listOf(
        PoiItem("mock1", "Mock Restaurant", "restaurant", 100, ""),
        PoiItem("mock2", "Mock Dessert", "dessert", 50, "")
    )
}