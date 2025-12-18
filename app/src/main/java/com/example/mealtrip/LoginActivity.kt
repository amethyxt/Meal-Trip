package com.example.mealtrip

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mealtrip.databinding.ActivityLoginBinding
import com.example.mealtrip.network.LoginRequest
import com.example.mealtrip.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. ปุ่มย้อนกลับ
        binding.btnBack.setOnClickListener {
            finish()
        }

        // 2. ปุ่มไปหน้า Register
        binding.btnRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 3. ปุ่ม Login
        binding.btnLogin.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "กรุณากรอก Email และ Password", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val request = LoginRequest(email, password)

                // เรียก API
                val response = RetrofitClient.apiService.loginUser(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    val user = loginResponse.user   // UserResponse?

                    if (user == null) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login สำเร็จ แต่ไม่พบข้อมูลผู้ใช้",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }

                    // -----------------------------
                    // ✅ เซฟข้อมูลลง SharedPreferences
                    // -----------------------------
                    val prefs = getSharedPreferences("USER_PREFS", MODE_PRIVATE)
                    prefs.edit()
                        .putString("USER_ID", user.id)
                        .putString("USERNAME", user.username)
                        .putString("EMAIL", user.email)                 // ถ้า field ชื่อ email
                        // ถ้า preferences เป็น List<String>? จะเก็บเป็น string ธรรมดาไว้ก่อน
                        .putString(
                            "PREFERENCES",
                            user.preferences?.joinToString(",") ?: ""
                        )
                        .apply()

                    Toast.makeText(
                        this@LoginActivity,
                        "Login สำเร็จ!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // -----------------------------
                    // เลือกหน้าไปต่อจาก preferences
                    // -----------------------------
                    val nextIntent: Intent = if (!user.preferences.isNullOrEmpty()) {
                        // มี preferences แล้ว -> ไปหน้า Home
                        Intent(this@LoginActivity, HomeActivity::class.java)
                    } else {
                        // ยังไม่มี -> ไปหน้าเลือกความชอบ
                        Intent(this@LoginActivity, InterestActivity::class.java)
                    }

                    // (ยังส่งต่อผ่าน Intent ไว้ก็ไม่เสียหาย เผื่อหน้าอื่นใช้)
                    nextIntent.putExtra("USER_ID", user.id)
                    nextIntent.putExtra("USER_NAME", user.username)
                    nextIntent.putExtra("USER_EMAIL", user.email)

                    startActivity(nextIntent)
                    finish()

                } else {
                    // กรณี Error (เช่น รหัสผิด)
                    try {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(
                            this@LoginActivity,
                            "Login ไม่ผ่าน: $errorBody",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login ไม่ผ่าน (Unknown Error)",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("LoginError", "Error", e)
            }
        }
    }
}
