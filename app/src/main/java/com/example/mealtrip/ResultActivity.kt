package com.example.mealtrip

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealtrip.databinding.ActivityResultBinding
import com.example.mealtrip.network.PoiResult
import com.example.mealtrip.network.ResultAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var resultAdapter: ResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val json = intent.getStringExtra("TRIP_RESULTS_JSON")

        val listType = object : TypeToken<List<PoiResult>>() {}.type
        val tripPackage: List<PoiResult> = if (!json.isNullOrBlank()) {
            try { Gson().fromJson(json, listType) } catch (e: Exception) { emptyList() }
        } else emptyList()

        resultAdapter = ResultAdapter(tripPackage)
        binding.rvResultList.apply {
            layoutManager = LinearLayoutManager(this@ResultActivity)
            adapter = resultAdapter
        }

        val totalScore = tripPackage.sumOf { it.score }
        val totalCost = tripPackage.sumOf { it.cost }

        binding.tvTotalScore.text = "Total Score: $totalScore ★"
        binding.tvRemainingBudget.text = "Total Cost: ฿$totalCost"

        // ✅ DONE (BACK TO HOME)
        binding.btnBackToHome.setOnClickListener {

            // 1) เอา USER_ID จาก Intent ก่อน
            var userId = intent.getStringExtra("USER_ID")
            var userName = intent.getStringExtra("USER_NAME")

            // 2) ถ้าไม่มี ค่อย fallback ไป prefs
            val prefs = getSharedPreferences("USER_PREFS", MODE_PRIVATE)
            if (userId.isNullOrBlank()) userId = prefs.getString("USER_ID", null)
            if (userName.isNullOrBlank()) userName = prefs.getString("USERNAME", null)

            val homeIntent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("USER_ID", userId)
                putExtra("USER_NAME", userName)
            }
            startActivity(homeIntent)
            finish()
        }
    }
}