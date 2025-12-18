package com.example.mealtrip

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealtrip.databinding.ActivityVotingBinding
import com.example.mealtrip.network.PoiItem
import com.example.mealtrip.network.RetrofitClient
import com.example.mealtrip.network.VoteAdapter
import com.example.mealtrip.network.VoteRequest
import com.google.gson.Gson
import kotlinx.coroutines.launch

class VotingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVotingBinding
    private lateinit var voteAdapter: VoteAdapter

    private var currentTripId: String? = null
    private var currentUserId: String? = null
    private var currentInviteCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVotingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentTripId = intent.getStringExtra("TRIP_ID")
        currentUserId = intent.getStringExtra("USER_ID")
        currentInviteCode = intent.getStringExtra("INVITE_CODE")

        if (currentTripId == null || currentUserId == null) {
            Toast.makeText(this, "Error: Missing Trip Data", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        binding.btnGetResults.setOnClickListener {
            getTripResults()
        }

        loadRealPoisFromApi()
    }

    private fun loadRealPoisFromApi() {
        val query = "restaurant in Bangkok"
        Toast.makeText(this, "Loading places...", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getRealPois(query)
                if (response.isSuccessful && response.body() != null) {
                    val realPois = response.body()!!
                    setupRecyclerView(realPois)
                } else {
                    Toast.makeText(
                        this@VotingActivity,
                        "Failed load POIs: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("VotingActivity", "API Error", e)
                Toast.makeText(this@VotingActivity, "Connection Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView(poiList: List<PoiItem>) {
        // 👇 ส่งทั้ง poi เข้าไปให้ callback
        voteAdapter = VoteAdapter(poiList) { poi, newScore ->
            voteOnPoi(poi, newScore)
        }

        binding.rvPoiList.apply {
            layoutManager = LinearLayoutManager(this@VotingActivity)
            adapter = voteAdapter
        }
    }

    // 👇 เปลี่ยนให้รับ PoiItem แทน poiId เฉยๆ
    private fun voteOnPoi(poi: PoiItem, score: Int) {
        lifecycleScope.launch {
            try {
                val request = VoteRequest(
                    trip_id = currentTripId!!,
                    user_id = currentUserId!!,
                    poi_id = poi.poiId,
                    score = score,
                    imageUrl = poi.imageUrl        // 👈 แนบ url รูปไปเก็บใน DB
                )

                val response = RetrofitClient.apiService.submitVote(request)

                if (response.isSuccessful) {
                    Log.d("DEBUG_TRIP", "Vote Success -> POI: ${poi.poiId} | Score: $score")
                } else {
                    Log.e("DEBUG_TRIP", "Vote Failed -> ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("DEBUG_TRIP", "Vote Error", e)
            }
        }
    }

    private fun getTripResults() {
        lifecycleScope.launch {
            try {
                Log.d("DEBUG_TRIP", "⚡ เริ่มดึงผลลัพธ์สำหรับ TripID = $currentTripId")

                val response = RetrofitClient.apiService.getTripResults(currentTripId!!)

                if (response.isSuccessful && response.body() != null) {
                    val results = response.body()!!

                    val tripPackage = results.tripPackage ?: emptyList()
                    Log.d("DEBUG_TRIP", "จำนวนสถานที่ที่ server ส่งกลับ = ${tripPackage.size}")

                    val jsonResults = Gson().toJson(tripPackage)
                    Log.d("DEBUG_TRIP", "ส่ง JSON ไปหน้า Result = $jsonResults")

                    val intent = Intent(this@VotingActivity, ResultActivity::class.java)
                    intent.putExtra("TRIP_RESULTS_JSON", jsonResults)
                    startActivity(intent)

                } else {
                    Log.e("DEBUG_TRIP", "Server error: ${response.code()}")
                    Toast.makeText(this@VotingActivity, "Server Error", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("DEBUG_TRIP", "Crash: ${e.message}", e)
                Toast.makeText(
                    this@VotingActivity,
                    "Error getting results",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
