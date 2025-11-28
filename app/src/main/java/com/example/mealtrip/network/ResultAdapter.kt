package com.example.mealtrip.network

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mealtrip.databinding.ItemPoiResultBinding

class ResultAdapter(
    private val resultList: List<PoiResult>
) : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    inner class ResultViewHolder(val binding: ItemPoiResultBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemPoiResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val poiResult = resultList[position]

        holder.binding.tvPoiName.text = poiResult.name
        holder.binding.tvPoiDetails.text = "${poiResult.type} • ฿${poiResult.cost}"
        holder.binding.tvFinalScore.text = "Score: ${poiResult.score} ★"

        // 1. โหลดรูปภาพ
        val imageUrl = if (poiResult.type.equals("restaurant", ignoreCase = true)) {
            "https://images.unsplash.com/photo-1559339352-11d035aa65de?w=500"
        } else {
            "https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?w=500"
        }

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .centerCrop()
            .into(holder.binding.ivPoiImage)

        // 2. เพิ่มฟีเจอร์: กดที่รายการแล้วเปิด Google Maps 🗺️
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val locationName = poiResult.name

            // สร้าง Uri เพื่อค้นหาสถานที่
            val gmmIntentUri = Uri.parse("geo:0,0?q=$locationName")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps") // บังคับเปิดด้วยแอป Maps

            try {
                context.startActivity(mapIntent)
            } catch (e: Exception) {
                // ถ้าเครื่องไม่มีแอป Maps ให้เปิดผ่าน Browser แทน
                val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$locationName")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                context.startActivity(webIntent)
            }
        }
    }

    override fun getItemCount(): Int {
        return resultList.size
    }
}