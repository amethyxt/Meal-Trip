package com.example.mealtrip.network

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mealtrip.databinding.ItemPoiResultBinding

class ResultAdapter(
    private var resultList: List<PoiResult>
) : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    inner class ResultViewHolder(val binding: ItemPoiResultBinding) :
        RecyclerView.ViewHolder(binding.root)

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

        val stay = poiResult.stayMinutes ?: 60
        val travel = poiResult.travelMinutesFromPrev ?: 0
        val startOffset = poiResult.startMinuteOffset ?: 0
        val endOffset = poiResult.endMinuteOffset ?: (startOffset + stay)

        holder.binding.tvPoiName.text = poiResult.name

        holder.binding.tvPoiDetails.text =
            "${poiResult.type} • ฿${poiResult.cost}\n" +
                    "Stay ~${stay} min • Travel from prev ~${travel} min\n" +
                    "Trip time: ${startOffset}–${endOffset} min"

        holder.binding.tvFinalScore.text = "Score: ${poiResult.score} ★"

        val imageUrl = when {
            !poiResult.imageUrl.isNullOrBlank() -> poiResult.imageUrl
            poiResult.type.equals("restaurant", ignoreCase = true) ->
                "https://images.unsplash.com/photo-1559339352-11d035aa65de?w=500"
            poiResult.type.equals("cafe", ignoreCase = true) ->
                "https://images.unsplash.com/photo-1517705008128-361805f42e86?w=500"
            else ->
                "https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?w=500"
        }

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .centerCrop()
            .into(holder.binding.ivPoiImage)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val locationName = poiResult.name

            val gmmIntentUri = Uri.parse("geo:0,0?q=$locationName")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            try {
                context.startActivity(mapIntent)
            } catch (e: Exception) {
                val webUri =
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=$locationName")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                context.startActivity(webIntent)
            }
        }
    }

    override fun getItemCount(): Int = resultList.size

    fun updateData(newList: List<PoiResult>) {
        resultList = newList
        notifyDataSetChanged()
    }
}
