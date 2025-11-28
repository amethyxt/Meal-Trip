package com.example.mealtrip

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mealtrip.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private var currentUserId: String? = null
    private var currentUserName: String? = null
    private var currentUserEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // "รับ" (Get) USER_ID, USER_NAME, และ USER_EMAIL ที่หน้า Home (HomeActivity) ส่งมาให้
        currentUserId = intent.getStringExtra("USER_ID")
        currentUserName = intent.getStringExtra("USER_NAME")
        currentUserEmail = intent.getStringExtra("USER_EMAIL")

        // "แสดงผล" (Display) ข้อมูล User ที่ "รับ" (Get) มา
        binding.tvUsername.text = "Username: $currentUserName"
        binding.tvEmail.text = "Email: $currentUserEmail"

        // "ดักฟัง" เมื่อปุ่ม "Logout" (btn_logout) ถูกกด
        binding.btnLogout.setOnClickListener {
            // "สร้าง" ตั๋ว (Intent) เพื่อ "เปิด" (กลับไป) หน้า MainActivity (หน้า 2 ปุ่ม)
            val intent = Intent(this, MainActivity::class.java)

            // (สำคัญ!) "เพิ่ม" (Add) "ธง" (Flags) 2 อันนี้
            // ...เพื่อ "ลบ" (Clear) "ประวัติ" (Backstack) (หน้า Home, Profile ฯลฯ) ทั้งหมด
            // ...และ "เริ่มใหม่" (Restart) ที่หน้า MainActivity
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            // "เปิด" หน้าใหม่ (และ "ลบ" ทุกอย่าง)
            startActivity(intent)
            finish() // "ปิด" หน้านี้ (ProfileActivity)
        }
    }
}