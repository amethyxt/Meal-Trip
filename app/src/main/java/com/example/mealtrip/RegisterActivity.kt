package com.example.mealtrip

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mealtrip.databinding.ActivityRegisterBinding
import com.example.mealtrip.network.RegisterRequest
import com.example.mealtrip.network.RetrofitClient
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. ปุ่มย้อนกลับ
        binding.btnBack.setOnClickListener {
            finish()
        }

        // 2. ปุ่มไปหน้า Login (ถ้ามีบัญชีอยู่แล้ว)
        binding.btnLoginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 3. ปุ่ม Sign Up (สมัครสมาชิก)
        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        // รับค่าจากช่องกรอก
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // เช็คว่ากรอกครบไหม
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "กรุณากรอกข้อมูลให้ครบทุกช่อง", Toast.LENGTH_SHORT).show()
            return
        }

        // เริ่มส่งข้อมูลไปสมัคร
        lifecycleScope.launch {
            try {
                val request = RegisterRequest(username, email, password)

                // เรียก API Register
                val response = RetrofitClient.apiService.registerUser(request)

                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@RegisterActivity, "สมัครสมาชิกสำเร็จ! กรุณา Login", Toast.LENGTH_LONG).show()

                    // สมัครเสร็จแล้ว ส่งกลับไปหน้า Login เพื่อให้ User กรอกรหัสเข้าใช้งาน
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish() // ปิดหน้า Register

                } else {
                    // กรณีสมัครไม่ผ่าน (เช่น Email ซ้ำ)
                    try {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(this@RegisterActivity, "สมัครไม่ผ่าน: $errorBody", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@RegisterActivity, "สมัครไม่ผ่าน (Unknown Error)", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("RegisterError", "Error", e)
            }
        }
    }
}