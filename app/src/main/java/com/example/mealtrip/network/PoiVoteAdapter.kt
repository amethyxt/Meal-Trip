package com.example.mealtrip.network

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mealtrip.databinding.ItemPoiVoteBinding

class PoiVoteAdapter(
    private val poiList: List<PoiItem>,
    private val onVoteClick: (PoiItem, Int) -> Unit
) : RecyclerView.Adapter<PoiVoteAdapter.PoiViewHolder>() {

    inner class PoiViewHolder(val binding: ItemPoiVoteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoiViewHolder {
        val binding = ItemPoiVoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PoiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PoiViewHolder, position: Int) {
        val poi = poiList[position]

        holder.binding.apply {
            // ชื่อสถานที่
            tvPoiName.text = poi.name

            // โหลดรูป
            Glide.with(ivPoiImage.context)
                .load(poi.imageUrl)
                .centerCrop()
                .into(ivPoiImage)

            // 1. ตัด Listener เก่าออก เพื่อกัน loop จาก RecyclerView
            ratingBar.setOnRatingBarChangeListener(null)

            // 2. กำหนดค่า default = 0 ดาว
            ratingBar.rating = 0f

            // 3. เมื่อ user กดดาวใหม่
            ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
                if (fromUser) {
                    val score = rating.toInt()

                    // ส่งคะแนนกลับให้ Activity (ไปยิง API)
                    onVoteClick(poi, score)
                }
            }
        }
    }

    override fun getItemCount(): Int = poiList.size
}
