package com.example.travelwise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travelwise.R
import com.example.travelwise.models.ItineraryActivityExtended

class FilteredActivityAdapter(
    private var activities: List<ItineraryActivityExtended>
) : RecyclerView.Adapter<FilteredActivityAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val ivActivityImage: ImageView? = itemView.findViewById(R.id.ivActivityImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_itinerary_activity, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val act = activities[position]
        holder.tvTime.text = act.time
        holder.tvTitle.text = act.title
        holder.tvDescription.text = act.description

        if (!act.image.isNullOrEmpty()) {
            holder.ivActivityImage?.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(act.image)
                .placeholder(R.drawable.image11)
                .error(R.drawable.image11)
                .centerCrop()
                .into(holder.ivActivityImage!!)
        } else if (act.hotel?.image != null && act.hotel.image.isNotEmpty()) {
            holder.ivActivityImage?.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(act.hotel.image)
                .placeholder(R.drawable.image11)
                .error(R.drawable.image11)
                .centerCrop()
                .into(holder.ivActivityImage!!)
        } else {
            holder.ivActivityImage?.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = activities.size

    fun update(list: List<ItineraryActivityExtended>) {
        activities = list
        notifyDataSetChanged()
    }
}
