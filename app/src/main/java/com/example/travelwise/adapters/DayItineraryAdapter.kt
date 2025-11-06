package com.example.travelwise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travelwise.R
import com.example.travelwise.models.DayItinerary
import com.example.travelwise.models.ActivityType

class DayItineraryAdapter(
    private val dayItineraries: List<DayItinerary>
) : RecyclerView.Adapter<DayItineraryAdapter.DayViewHolder>() {

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDayNumber: TextView = itemView.findViewById(R.id.tvDayNumber)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvDateFull: TextView = itemView.findViewById(R.id.tvDateFull)
        val llActivities: LinearLayout = itemView.findViewById(R.id.llActivities)
        val ivDayImage: ImageView = itemView.findViewById(R.id.ivDayImage)
        val viewDayConnector: View = itemView.findViewById(R.id.viewDayConnector)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day_itinerary, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val dayItinerary = dayItineraries[position]

        // Set "Day 1", "Day 2", etc. in the circle badge
        holder.tvDayNumber.text = "Day ${dayItinerary.dayNumber}"

        // Set title as "Day 1" or custom title
        holder.tvDate.text = "Day ${dayItinerary.dayNumber}"

        // Set date in format like "06 Nov"
        holder.tvDateFull.text = dayItinerary.date

        // Hide connector line for last day
        holder.viewDayConnector.visibility = if (position == dayItineraries.size - 1) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }

        // Load day image
        if (dayItinerary.imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(dayItinerary.imageUrl)
                .placeholder(R.drawable.image11)
                .error(R.drawable.image11)
                .centerCrop()
                .into(holder.ivDayImage)
        } else {
            holder.ivDayImage.setImageResource(R.drawable.image11)
        }

        // Remove existing activity views
        holder.llActivities.removeAllViews()

        // Add activities for this day
        dayItinerary.activities.forEachIndexed { index, activity ->
            val activityView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.item_itinerary_activity, holder.llActivities, false)

            val tvTime: TextView = activityView.findViewById(R.id.tvTime)
            val tvTitle: TextView = activityView.findViewById(R.id.tvTitle)
            val tvDescription: TextView = activityView.findViewById(R.id.tvDescription)
            val viewDot: View = activityView.findViewById(R.id.viewDot)
            val viewConnector: View = activityView.findViewById(R.id.viewConnector)

            tvTime.text = activity.time
            tvTitle.text = activity.title
            tvDescription.text = activity.description

            // Show/hide description if empty
            tvDescription.visibility = if (activity.description.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }

            // Hide connector for last activity
            viewConnector.visibility = if (index == dayItinerary.activities.size - 1) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }

            // Color dot based on activity type
            val dotDrawable = when (activity.type) {
                ActivityType.FLIGHT -> R.drawable.circle_dot_primary
                ActivityType.HOTEL -> R.drawable.circle_dot_teal
                ActivityType.ATTRACTION -> R.drawable.circle_dot_purple
                else -> R.drawable.circle_dot_grey
            }
            viewDot.setBackgroundResource(dotDrawable)

            holder.llActivities.addView(activityView)
        }
    }

    override fun getItemCount() = dayItineraries.size
}