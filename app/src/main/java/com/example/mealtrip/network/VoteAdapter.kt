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

    // ✅ เก็บคะแนนที่ user โหวตไว้ (key = id หรือ name)
    private val voteMap = mutableMapOf<String, Int>()

    inner class VoteViewHolder(val binding: ItemPoiVoteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoteViewHolder {
        val binding = ItemPoiVoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VoteViewHolder, position: Int) {
        val poi = poiList[position]

        // ✅ สร้าง key แบบไม่ใช้ poi.id (เพราะไม่มีใน model)
        val key = "${poi.name}_${poi.type}_${poi.cost}"

        holder.binding.apply {
            tvPoiName.text = poi.name
            tvPoiDetails.text = "${poi.type} • ฿${poi.cost}"

            Glide.with(ivPoiImage.context)
                .load(poi.imageUrl)
                .centerCrop()
                .into(ivPoiImage)

            // ✅ ดึงคะแนนเดิม ถ้าไม่เคยให้คะแนนจะเป็น 0
            val savedScore = voteMap[key] ?: 0

            ratingBar.setOnRatingBarChangeListener(null)
            ratingBar.rating = savedScore.toFloat()

            ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
                voteMap[key] = rating.toInt()
            }
        }
    }

    override fun getItemCount(): Int = poiList.size

    // ✅ เอาไว้ให้หน้า VotingActivity ดึงคะแนนทั้งหมดตอนกด "Get Results"
    fun getVotes(): Map<String, Int> = voteMap
}
