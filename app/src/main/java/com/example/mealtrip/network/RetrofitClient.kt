package com.example.mealtrip.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// นี่คือ "โรงงาน" (Singleton Object) ที่จะสร้าง Retrofit ให้เรา
// เราจะเรียกใช้ "โรงงาน" นี้จากทุกที่ในแอป
object RetrofitClient {

    // 1. "ที่อยู่" (Base URL) ของ Server
    // (เราใช้ 10.0.2.2 แทน localhost สำหรับ Android Emulator)
    private const val BASE_URL = "http://10.0.2.2:3000/"

    // 2. "สร้าง" บุรุษไปรษณีย์ (Retrofit)
    // (lazy คือการบอกว่า "อย่าเพิ่งสร้าง จนกว่าจะมีคนเรียกใช้ครั้งแรก")
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // <-- บอกว่า Server อยู่ที่ไหน
            .addConverterFactory(GsonConverterFactory.create()) // <-- บอกให้ใช้ "ล่าม" (Gson)
            .build()
    }

    // 3. "สร้าง" พิมพ์เขียว API (ApiService) ให้พร้อมใช้งาน
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}