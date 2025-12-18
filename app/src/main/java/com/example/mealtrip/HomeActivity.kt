package com.example.mealtrip

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mealtrip.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    // เก็บ user ปัจจุบัน
    private var currentUserId: String? = null
    private var currentUserName: String? = null
    private var currentUserEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // -----------------------------
        // 1) ดึงข้อมูลจาก SharedPreferences ก่อน
        // -----------------------------
        val prefs = getSharedPreferences("USER_PREFS", MODE_PRIVATE)
        currentUserId = prefs.getString("USER_ID", null)
        currentUserName = prefs.getString("USERNAME", null)
        currentUserEmail = prefs.getString("EMAIL", null)

        // -----------------------------
        // 2) fallback จาก Intent (รองรับหลาย key กันหลุด)
        // -----------------------------
        if (currentUserId.isNullOrEmpty()) {
            currentUserId = intent.getStringExtra("USER_ID")
        }

        if (currentUserName.isNullOrEmpty()) {
            currentUserName = intent.getStringExtra("USERNAME")
                ?: intent.getStringExtra("USER_NAME")
        }

        if (currentUserEmail.isNullOrEmpty()) {
            currentUserEmail = intent.getStringExtra("EMAIL")
                ?: intent.getStringExtra("USER_EMAIL")
        }

        // ถ้าไม่มี userId เลย = ยังไม่ได้ login / prefs โดนลบ
        if (currentUserId.isNullOrEmpty()) {
            Toast.makeText(this, "Session หมดอายุ กรุณา Login ใหม่", Toast.LENGTH_SHORT).show()
            goToMainAndClearStack()
            return
        }

        val displayName = currentUserName ?: "User"
        binding.tvWelcome.text = "Hi, $displayName! 👋"

        // -----------------------------
        // 3) ปุ่ม Create Trip
        // -----------------------------
        binding.btnCreateTrip.setOnClickListener {
            val uid = currentUserId
            if (!uid.isNullOrEmpty()) {
                val intent = Intent(this, CreateTripActivity::class.java)
                intent.putExtra("USER_ID", uid)
                startActivity(intent)
            } else {
                Toast.makeText(this, "User Error: กรุณา Login ใหม่", Toast.LENGTH_SHORT).show()
                goToMainAndClearStack()
            }
        }

        // -----------------------------
        // 4) ปุ่ม Join Trip
        // -----------------------------
        binding.btnJoinTrip.setOnClickListener {
            val uid = currentUserId
            if (!uid.isNullOrEmpty()) {
                val intent = Intent(this, JoinTripActivity::class.java)
                intent.putExtra("USER_ID", uid)
                startActivity(intent)
            } else {
                Toast.makeText(this, "User Error: กรุณา Login ใหม่", Toast.LENGTH_SHORT).show()
                goToMainAndClearStack()
            }
        }

        // -----------------------------
        // 5) ปุ่ม Profile (ส่งข้อมูลไปด้วย)
        // -----------------------------
        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("USER_ID", currentUserId)
            intent.putExtra("USER_NAME", currentUserName)
            intent.putExtra("USER_EMAIL", currentUserEmail)
            startActivity(intent)
        }
    }

    private fun goToMainAndClearStack() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
