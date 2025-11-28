package com.example.mealtrip.network

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mealtrip.databinding.ItemPoiVoteBinding

class VoteAdapter(
    private val poiList: List<PoiItem>,
    private val onVoteClick: (PoiItem, Int) -> Unit
) : RecyclerView.Adapter<VoteAdapter.VoteViewHolder>() {

    inner class VoteViewHolder(val binding: ItemPoiVoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoteViewHolder {
        val binding = ItemPoiVoteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VoteViewHolder, position: Int) {
        val poi = poiList[position]

        holder.binding.apply {
            // 1. ใส่ชื่อ
            tvPoiName.text = poi.name

            // ✅ แก้ตรงนี้: เอาเครื่องหมาย // ออก เพื่อให้มันทำงานครับ
            tvPoiDetails.text = "${poi.type} • ฿${poi.cost}"

            // 2. ใส่รูป
            Glide.with(holder.itemView.context)
                .load(poi.imageUrl)
                .centerCrop()
                .into(ivPoiImage)

            // 3. ล้าง Listener เก่าออกก่อน
            ratingBar.setOnRatingBarChangeListener(null)

            // 4. ดึงค่าคะแนนเดิมมาโชว์
            ratingBar.rating = poi.myScore.toFloat()

            // 5. ดักจับการกดดาว
            ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
                if (fromUser) {
                    poi.myScore = rating.toInt()
                    onVoteClick(poi, rating.toInt())
                }
            }
        }
    }

    override fun getItemCount() = poiList.size
}