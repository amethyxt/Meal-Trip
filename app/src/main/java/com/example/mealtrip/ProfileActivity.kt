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

        // 1) รับค่าจาก Intent ก่อน
        currentUserId = intent.getStringExtra("USER_ID")
        currentUserName = intent.getStringExtra("USER_NAME")
        currentUserEmail = intent.getStringExtra("USER_EMAIL")

        // 2) fallback จาก SharedPreferences (กันกรณีไม่ได้ส่งมา)
        val prefs = getSharedPreferences("USER_PREFS", MODE_PRIVATE)
        if (currentUserId.isNullOrEmpty()) currentUserId = prefs.getString("USER_ID", null)
        if (currentUserName.isNullOrEmpty()) currentUserName = prefs.getString("USERNAME", null)
        if (currentUserEmail.isNullOrEmpty()) currentUserEmail = prefs.getString("EMAIL", null)

        // 3) แสดงผลบน UI
        val name = currentUserName ?: "Unknown user"
        val email = currentUserEmail ?: "-"

        // name+email ใต้รูป
        binding.tvUsername.text = name
        binding.tvEmail.text = email

        // ในกล่อง Account details
        binding.tvUsernameDetail.text = name
        binding.tvEmailDetail.text = email

        // 4) ปุ่ม Back (ถ้า layout มี btnBack)
        // ถ้าโปรเจคยังไม่มีปุ่ม back จริง ๆ จะคอมเมนต์ไว้กันแอปพัง
        try {
            binding.btnBack.setOnClickListener { finish() }
        } catch (_: Exception) {
            // ไม่มี btnBack ใน XML ก็ไม่เป็นไร
        }

        // 5) ปุ่ม Logout: ล้าง session + กลับ Main
        binding.btnLogout.setOnClickListener {
            prefs.edit().clear().apply()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
