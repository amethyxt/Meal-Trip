package com.example.mealtrip

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mealtrip.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var currentUserId: String? = null
    private var currentUserName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. รับค่า User ที่ส่งมาจากหน้า Login
        currentUserId = intent.getStringExtra("USER_ID")
        currentUserName = intent.getStringExtra("USER_NAME") ?: "User" // ถ้าไม่มีชื่อ ให้ใช้คำว่า User

        // 2. แสดงชื่อคน Login ตรงหัวข้อ
        binding.tvWelcome.text = "Hi, $currentUserName! 👋"

        // 3. ปุ่ม Create Trip (ไปหน้าสร้างทริป)
        binding.btnCreateTrip.setOnClickListener {
            if (currentUserId != null) {
                val intent = Intent(this, CreateTripActivity::class.java)
                intent.putExtra("USER_ID", currentUserId) // ส่ง ID ไปด้วย สำคัญมาก!
                startActivity(intent)
            } else {
                Toast.makeText(this, "User Error: กรุณา Login ใหม่", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        // 4. ปุ่ม Join Trip (ไปหน้าใส่รหัส)
        binding.btnJoinTrip.setOnClickListener {
            if (currentUserId != null) {
                val intent = Intent(this, JoinTripActivity::class.java) // อย่าลืมสร้างไฟล์นี้หรือแก้ชื่อให้ตรง
                intent.putExtra("USER_ID", currentUserId)
                startActivity(intent)
            }
        }

        // 5. ปุ่ม Profile (ถ้ามีหน้า Profile)
        binding.btnProfile.setOnClickListener {
            // ตอนนี้ให้แสดง Toast เล่นๆ ไปก่อน หรือจะลิงก์ไปหน้า ProfileActivity ก็ได้
            Toast.makeText(this, "Profile of $currentUserName", Toast.LENGTH_SHORT).show()
        }
    }
}