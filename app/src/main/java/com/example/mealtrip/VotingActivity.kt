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
// ▼▼▼ เปลี่ยน Import มาใช้ VoteAdapter ตัวใหม่ ▼▼▼
import com.example.mealtrip.network.VoteAdapter
// ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲
import com.example.mealtrip.network.RetrofitClient
import com.example.mealtrip.network.VoteRequest
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VotingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVotingBinding

    // ▼▼▼ เปลี่ยนตรงนี้เป็น VoteAdapter ▼▼▼
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

        binding.tvTitle.text = if (currentInviteCode != null) "Code: $currentInviteCode" else "Vote POIs"

        binding.btnGetResults.setOnClickListener {
            getTripResults()
        }

        loadRealPoisFromApi()
    }

    private fun loadRealPoisFromApi() {
        // ใช้คำค้นหาที่กว้างขึ้น
        val query = "restaurant in Bangkok"
        Toast.makeText(this, "Loading places...", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getRealPois(query)
                if (response.isSuccessful && response.body() != null) {
                    val realPois = response.body()!!
                    setupRecyclerView(realPois)
                } else {
                    Toast.makeText(this@VotingActivity, "Failed load POIs: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("VotingActivity", "API Error", e)
                Toast.makeText(this@VotingActivity, "Connection Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView(poiList: List<PoiItem>) {
        // ▼▼▼ เปลี่ยนการสร้าง Adapter เป็น VoteAdapter ▼▼▼
        voteAdapter = VoteAdapter(poiList) { poi, newScore ->
            voteOnPoi(poi.poiId, newScore)
        }

        binding.rvPoiList.apply {
            layoutManager = LinearLayoutManager(this@VotingActivity)
            adapter = voteAdapter
        }
    }

    private fun voteOnPoi(poiId: String, score: Int) {
        lifecycleScope.launch {
            try {
                val request = VoteRequest(currentTripId!!, currentUserId!!, poiId, score)
                val response = RetrofitClient.apiService.submitVote(request)

                if (response.isSuccessful) {
                    Log.d("DEBUG_TRIP", "Vote Success -> POI: $poiId | Score: $score")
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
                Log.d("DEBUG_TRIP", "------- เริ่มดึงผลลัพธ์ -------")

                val response = RetrofitClient.apiService.getTripResults(currentTripId!!)

                if (response.isSuccessful && response.body() != null) {
                    val results = response.body()!!

                    // ดู Log ข้อมูลที่ได้
                    Log.d("DEBUG_TRIP", "จำนวนสถานที่: ${results.tripPackage.size}")

                    val intent = Intent(this@VotingActivity, ResultActivity::class.java)
                    val jsonResults = Gson().toJson(results.tripPackage)
                    intent.putExtra("TRIP_RESULTS_JSON", jsonResults)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@VotingActivity, "Server Error", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("DEBUG_TRIP", "Crash: ${e.message}")
                Toast.makeText(this@VotingActivity, "Error getting results", Toast.LENGTH_SHORT).show()
            }
        }
    }
}