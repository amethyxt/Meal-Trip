package com.example.mealtrip

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mealtrip.databinding.ActivityLobbyBinding
import com.example.mealtrip.network.GetMembersResponse
import com.example.mealtrip.network.RetrofitClient
import com.example.mealtrip.network.StartTripResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LobbyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLobbyBinding
    private var tripId: String? = null
    private var userId: String? = null
    private var inviteCode: String? = null
    private var isHost: Boolean = false

    // ตัวช่วยสำหรับการ "วนลูป" เช็คข้อมูล (Polling)
    private val handler = Handler(Looper.getMainLooper())
    private val fetchRunnable = object : Runnable {
        override fun run() {
            fetchMembersAndStatus()
            // สั่งให้ทำซ้ำอีกครั้งใน 3 วินาที (3000 ms)
            handler.postDelayed(this, 3000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. รับข้อมูลจากหน้าก่อน
        tripId = intent.getStringExtra("TRIP_ID")
        userId = intent.getStringExtra("USER_ID")
        inviteCode = intent.getStringExtra("INVITE_CODE")
        isHost = intent.getBooleanExtra("IS_HOST", false) // รับค่าว่าเป็น Host หรือไม่

        // 2. ตั้งค่าหน้าจอ
        binding.tvRoomCode.text = "Code: ${inviteCode ?: "..."}"

        // ถ้าเป็น Host ให้โชว์ปุ่ม Start
        if (isHost) {
            binding.btnStartVoting.visibility = View.VISIBLE
            binding.tvStatus.text = "You are the Host. Press start when ready!"

            binding.btnStartVoting.setOnClickListener {
                startTrip()
            }
        } else {
            binding.btnStartVoting.visibility = View.GONE
            binding.tvStatus.text = "Waiting for host to start..."
        }
    }

    override fun onResume() {
        super.onResume()
        // เริ่มเช็คชื่อเมื่อเปิดหน้าจอ
        startPolling()
    }

    override fun onPause() {
        super.onPause()
        // หยุดเช็คชื่อเมื่อปิด/สลับหน้าจอ (ประหยัดแบต)
        stopPolling()
    }

    private fun startPolling() {
        handler.post(fetchRunnable)
    }

    private fun stopPolling() {
        handler.removeCallbacks(fetchRunnable)
    }

    private fun fetchMembersAndStatus() {
        if (tripId == null) return

        RetrofitClient.apiService.getTripMembers(tripId!!).enqueue(object : Callback<GetMembersResponse> {
            override fun onResponse(call: Call<GetMembersResponse>, response: Response<GetMembersResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!

                    // 1. เช็คสถานะทริป
                    if (data.tripStatus == "voting") {
                        goToVotingScreen()
                    } else {
                        // 2. อัปเดตรายชื่อเพื่อน
                        updateMemberList(data.members)
                    }
                }
            }
            override fun onFailure(call: Call<GetMembersResponse>, t: Throwable) {
                Log.e("Lobby", "Error fetching members: ${t.message}")
            }
        })
    }

    private fun updateMemberList(members: List<com.example.mealtrip.network.MemberInfo>) {
        binding.tvMemberCount.text = "Members (${members.size})"

        // วิธีบ้านๆ: เอาชื่อมาต่อกันเป็น String ยาวๆ (ในอนาคตค่อยใช้ RecyclerView ก็ได้)
        val names = members.joinToString(separator = "\n") {
            "👤 ${it.username}" + if (it.userId == userId) " (You)" else ""
        }
        binding.tvMemberList.text = names
    }

    private fun startTrip() {
        if (tripId == null) return

        binding.btnStartVoting.isEnabled = false // กันกดย้ำ
        Toast.makeText(this, "Starting trip...", Toast.LENGTH_SHORT).show()

        RetrofitClient.apiService.startTrip(tripId!!).enqueue(object : Callback<StartTripResponse> {
            override fun onResponse(call: Call<StartTripResponse>, response: Response<StartTripResponse>) {
                if (response.isSuccessful) {
                    // พอ Host กดเริ่ม Server จะเปลี่ยน status เป็น 'voting'
                    // เดี๋ยว Loop 'fetchMembersAndStatus' รอบหน้าจะจับได้เอง แล้วพาไปหน้าโหวต
                } else {
                    binding.btnStartVoting.isEnabled = true
                    Toast.makeText(this@LobbyActivity, "Failed to start", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<StartTripResponse>, t: Throwable) {
                binding.btnStartVoting.isEnabled = true
                Toast.makeText(this@LobbyActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun goToVotingScreen() {
        stopPolling() // หยุดเช็คชื่อ
        val intent = Intent(this, VotingActivity::class.java)
        intent.putExtra("TRIP_ID", tripId)
        intent.putExtra("USER_ID", userId)
        intent.putExtra("INVITE_CODE", inviteCode)
        startActivity(intent)
        finish()
    }
}