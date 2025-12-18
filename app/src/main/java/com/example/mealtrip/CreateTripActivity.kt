package com.example.mealtrip

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mealtrip.databinding.ActivityCreateTripBinding
import com.example.mealtrip.network.CreateTripRequest
import com.example.mealtrip.network.JoinTripRequest
import com.example.mealtrip.network.RetrofitClient
import kotlinx.coroutines.launch

class CreateTripActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateTripBinding
    private var currentUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUserId = intent.getStringExtra("USER_ID")

        binding.btnBack.setOnClickListener { finish() }

        binding.btnCreateLobby.setOnClickListener {
            createTrip()
        }
    }

    private fun createTrip() {
        val tripName = binding.etTripName.text.toString().trim()
        val budgetStr = binding.etBudget.text.toString().trim()

        if (tripName.isEmpty() || budgetStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val budget = budgetStr.toIntOrNull() ?: 0
        Log.d("CreateTrip", "Budget sending: $budget")

        // เงื่อนไข demo: ขอ restaurant อย่างน้อย 1 ที่
        val constraints = mapOf(
            "categories" to listOf(
                mapOf("type" to "restaurant", "count" to 1)
            )
        )

        lifecycleScope.launch {
            try {
                // 1) สร้างทริป
                val createRequest = CreateTripRequest(
                    tripName,
                    currentUserId!!,
                    budget,
                    constraints
                )
                val createResponse = RetrofitClient.apiService.createTrip(createRequest)

                if (createResponse.isSuccessful && createResponse.body() != null) {
                    val tripData = createResponse.body()!!
                    val inviteCode = tripData.inviteCode
                    val tripId = tripData.id

                    Toast.makeText(
                        this@CreateTripActivity,
                        "Trip Created! Code: $inviteCode",
                        Toast.LENGTH_SHORT
                    ).show()

                    // 2) Auto join ทริปตัวเอง
                    val joinRequest = JoinTripRequest(inviteCode, currentUserId!!)
                    val joinResponse = RetrofitClient.apiService.joinTrip(joinRequest)

                    if (joinResponse.isSuccessful) {
                        // 3) ไปหน้า Lobby
                        val intent = Intent(this@CreateTripActivity, LobbyActivity::class.java)
                        intent.putExtra("TRIP_ID", tripId)
                        intent.putExtra("USER_ID", currentUserId)
                        intent.putExtra("INVITE_CODE", inviteCode)
                        intent.putExtra("IS_HOST", true)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@CreateTripActivity,
                            "Failed to join lobby",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@CreateTripActivity,
                        "Failed to create trip",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@CreateTripActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }
    }
}
