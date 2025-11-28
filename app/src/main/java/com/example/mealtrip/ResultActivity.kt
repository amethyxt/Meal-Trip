package com.example.mealtrip

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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

        setupRecyclerView()
        loadData()

        binding.btnBackToHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupRecyclerView() {
        resultAdapter = ResultAdapter(emptyList())
        binding.rvResultList.apply {
            layoutManager = LinearLayoutManager(this@ResultActivity)
            adapter = resultAdapter
        }
    }

    private fun loadData() {
        val jsonResults = intent.getStringExtra("TRIP_RESULTS_JSON")

        if (!jsonResults.isNullOrEmpty()) {
            try {
                val type = object : TypeToken<List<PoiResult>>() {}.type
                val results: List<PoiResult> = Gson().fromJson(jsonResults, type)

                // คำนวณยอดรวม
                val totalScore = results.sumOf { it.score }
                val totalCost = results.sumOf { it.cost }

                binding.tvTotalScore.text = "Total Score: $totalScore ★"
                binding.tvRemainingBudget.text = "Total Cost: ฿$totalCost"

                if (results.isNotEmpty()) {
                    resultAdapter = ResultAdapter(results)
                    binding.rvResultList.adapter = resultAdapter
                } else {
                    // ถ้า Server ยังส่ง 0 มา จะขึ้นเตือนตรงนี้
                    Toast.makeText(this, "No matching places found from Server.", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            binding.tvTotalScore.text = "No Data"
        }
    }
}