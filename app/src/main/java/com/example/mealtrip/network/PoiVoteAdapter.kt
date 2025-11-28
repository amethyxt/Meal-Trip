package com.example.mealtrip.network

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
// ตรวจสอบชื่อ Binding ของคุณให้ถูก (เช่น ItemPoiVoteBinding)
import com.example.mealtrip.databinding.ItemPoiVoteBinding

class PoiVoteAdapter(
    private val poiList: List<PoiItem>,
    private val onVoteClick: (PoiItem, Int) -> Unit
) : RecyclerView.Adapter<PoiVoteAdapter.PoiViewHolder>() {

    inner class PoiViewHolder(val binding: ItemPoiVoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoiViewHolder {
        val binding = ItemPoiVoteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PoiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PoiViewHolder, position: Int) {
        val poi = poiList[position]

        holder.binding.apply {
            tvPoiName.text = poi.name
            // tvPoiDetails.text = ... (ถ้ามี)

            // โหลดรูป
            Glide.with(ivPoiImage)
                .load(poi.imageUrl)
                .centerCrop()
                .into(ivPoiImage)

            // ▼▼▼ 1. ล้าง Listener เก่าออกก่อน (สำคัญมาก กันบั๊ก) ▼▼▼
            ratingBar.setOnRatingBarChangeListener(null)

            // ▼▼▼ 2. ดึงค่าคะแนนที่จำไว้ มาแสดง ▼▼▼
            ratingBar.rating = poi.myScore.toFloat()

            // ▼▼▼ 3. เมื่อกดดาวใหม่ ให้บันทึกค่าลงตัวแปรทันที ▼▼▼
            ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
                if (fromUser) {
                    val score = rating.toInt()

                    // บันทึก!
                    poi.myScore = score

                    // ส่ง API
                    onVoteClick(poi, score)
                }
            }
        }
    }

    override fun getItemCount() = poiList.size
}