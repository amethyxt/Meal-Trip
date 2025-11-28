package com.example.mealtrip

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView // ใช้ TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mealtrip.databinding.ActivityInterestBinding
import com.example.mealtrip.network.PreferencesRequest
import com.example.mealtrip.network.RetrofitClient
import com.example.mealtrip.network.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InterestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInterestBinding
    private var currentUserId: String? = null
    private var currentUserName: String? = null
    private val selectedInterests = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityInterestBinding.inflate(layoutInflater)
            setContentView(binding.root)

            currentUserId = intent.getStringExtra("USER_ID")
            currentUserName = intent.getStringExtra("USER_NAME")

            // ส่ง TextView เข้าไป
            setupCard(binding.cardFood, "street_food")
            setupCard(binding.cardCafe, "cafe")
            setupCard(binding.cardTemple, "temple")
            setupCard(binding.cardNature, "nature")
            setupCard(binding.cardShopping, "shopping")
            setupCard(binding.cardCulture, "culture")

            binding.btnSaveInterest.setOnClickListener {
                savePreferences()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error UI: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // ฟังก์ชันนี้รับ TextView
    private fun setupCard(card: TextView, type: String) {
        card.setOnClickListener {
            if (selectedInterests.contains(type)) {
                selectedInterests.remove(type)
                // ไม่เลือก: สีเทาอ่อน ตัวหนังสือดำ
                card.setBackgroundColor(Color.parseColor("#EEEEEE"))
                card.setTextColor(Color.BLACK)
            } else {
                selectedInterests.add(type)
                // เลือกแล้ว: สีม่วง ตัวหนังสือขาว
                card.setBackgroundColor(Color.parseColor("#6200EE"))
                card.setTextColor(Color.WHITE)
            }
        }
    }

    private fun savePreferences() {
        if (currentUserId == null) return

        val preferencesList = selectedInterests.toList()
        val request = PreferencesRequest(preferencesList)

        RetrofitClient.apiService.savePreferences(currentUserId!!, request)
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@InterestActivity, "บันทึกเรียบร้อย!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@InterestActivity, HomeActivity::class.java)
                        intent.putExtra("USER_ID", currentUserId)
                        intent.putExtra("USER_NAME", currentUserName)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@InterestActivity, "บันทึกไม่สำเร็จ", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(this@InterestActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}