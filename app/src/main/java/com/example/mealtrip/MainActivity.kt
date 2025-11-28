package com.example.mealtrip

import android.content.Intent // <-- (2) เพิ่ม Import Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mealtrip.databinding.ActivityMainBinding // <-- (1) เพิ่ม Import ViewBinding

class MainActivity : AppCompatActivity() {

    // (1) สร้างตัวแปร binding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // (1) "เปิด" ViewBinding ให้ทำงาน
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // (ลบโค้ด enableEdgeToEdge และ ViewCompat เก่าออก)

        // ▼▼▼ (3. นี่คือ "สมอง" ที่เรา "อัปเดต" ครับ) ▼▼▼

        // "ดักฟัง" ปุ่ม "ไปหน้าล็อกอิน"
        binding.btnGoToLogin.setOnClickListener {
            // "สร้าง" ตั๋ว (Intent) เพื่อ "เปิด" หน้า LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            // "เปิด" หน้าใหม่
            startActivity(intent)
        }

        // "ดักฟัง" ปุ่ม "ไปหน้าสมัครสมาชิก" (อันนี้มีอยู่แล้ว)
        binding.btnGoToRegister.setOnClickListener {
            // "สร้าง" ตั๋ว (Intent) เพื่อ "เปิด" หน้า RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            // "เปิด" หน้าใหม่
            startActivity(intent)
        }
    }
}