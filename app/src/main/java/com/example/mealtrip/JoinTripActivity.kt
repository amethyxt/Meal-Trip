package com.example.mealtrip

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mealtrip.databinding.ActivityJoinTripBinding
import com.example.mealtrip.network.JoinTripRequest
import com.example.mealtrip.network.RetrofitClient
import kotlinx.coroutines.launch

class JoinTripActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinTripBinding
    private var currentUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUserId = intent.getStringExtra("USER_ID")

        binding.btnBack.setOnClickListener { finish() }

        binding.btnJoin.setOnClickListener {
            joinTrip()
        }
    }

    private fun joinTrip() {
        val code = binding.etInviteCode.text.toString().trim().uppercase()

        if (code.isEmpty()) {
            Toast.makeText(this, "Please enter code", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // ยิง API ขอเข้าห้อง
                val request = JoinTripRequest(code, currentUserId!!)
                val response = RetrofitClient.apiService.joinTrip(request)

                if (response.isSuccessful && response.body() != null) {
                    val tripId = response.body()!!.tripId

                    Toast.makeText(this@JoinTripActivity, "Joined Success!", Toast.LENGTH_SHORT).show()

                    // ไปหน้า Lobby (ในฐานะ Guest -> IS_HOST = false)
                    val intent = Intent(this@JoinTripActivity, LobbyActivity::class.java)
                    intent.putExtra("TRIP_ID", tripId)
                    intent.putExtra("USER_ID", currentUserId)
                    intent.putExtra("INVITE_CODE", code)
                    intent.putExtra("IS_HOST", false) // Guest

                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@JoinTripActivity, "Code not found or invalid", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@JoinTripActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}