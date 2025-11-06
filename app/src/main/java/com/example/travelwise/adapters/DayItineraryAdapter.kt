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
import com.example.travelwise.models.ItineraryActivityExtended
import com.example.travelwise.models.ItineraryActivity

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
        val tvSummaryCounts: TextView = itemView.findViewById(R.id.tvSummaryCounts)
        val llTags: LinearLayout = itemView.findViewById(R.id.llTags)
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
        holder.llTags.removeAllViews()

        // Group activities into sections and render each section with a header
        val order = listOf(
            ActivityType.FLIGHT,
            ActivityType.HOTEL,
            ActivityType.ATTRACTION,
            ActivityType.MEAL,
            ActivityType.TRANSPORT,
            ActivityType.GENERAL
        )

        val grouped = dayItinerary.activities.groupBy { it.type }

        order.forEach { typeKey ->
            val listForType = grouped[typeKey] ?: emptyList()
            if (listForType.isNotEmpty()) {
                // Inflate and add section header
                val header = LayoutInflater.from(holder.itemView.context)
                    .inflate(R.layout.item_activity_section_header, holder.llActivities, false)
                val tvSectionTitle: TextView? = header.findViewById(R.id.tvSectionTitle)
                tvSectionTitle?.text = when (typeKey) {
                    ActivityType.FLIGHT -> "Flights"
                    ActivityType.HOTEL -> "Hotels"
                    ActivityType.ATTRACTION -> "Places & Attractions"
                    ActivityType.MEAL -> "Food & Dining"
                    ActivityType.TRANSPORT -> "Transport"
                    else -> "Other"
                }
                holder.llActivities.addView(header)

                // Add activities under this section
                listForType.forEachIndexed { index, activity ->
                    // Ensure we have the extended activity model (backwards compatible with older ItineraryActivity)
                    val act = when (activity) {
                        is com.example.travelwise.models.ItineraryActivityExtended -> activity
                        is com.example.travelwise.models.ItineraryActivity -> com.example.travelwise.models.ItineraryActivityExtended(
                            time = activity.time,
                            title = activity.title,
                            description = activity.description,
                            type = activity.type
                        )
                        else -> null
                    } ?: return@forEachIndexed
                    val activityView = LayoutInflater.from(holder.itemView.context)
                        .inflate(R.layout.item_itinerary_activity, holder.llActivities, false)

                    val tvTime: TextView = activityView.findViewById(R.id.tvTime)
                    val tvTitle: TextView = activityView.findViewById(R.id.tvTitle)
                    val tvDescription: TextView = activityView.findViewById(R.id.tvDescription)
                    val viewDot: View = activityView.findViewById(R.id.viewDot)
                    val viewConnector: View = activityView.findViewById(R.id.viewConnector)
                    val ivActivityImage: ImageView? = activityView.findViewById(R.id.ivActivityImage)

                    tvTime.text = act.time
                    tvTitle.text = act.title
                    tvDescription.text = act.description

                    // Show/hide description if empty
                    tvDescription.visibility = if (act.description.isNullOrEmpty()) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }

                    // Hide connector for last activity in overall list
                    viewConnector.visibility = if (index == listForType.size - 1) {
                        View.INVISIBLE
                    } else {
                        View.VISIBLE
                    }

                    // Color dot based on activity type
                    val dotDrawable = when (act.type) {
                        ActivityType.FLIGHT -> R.drawable.circle_dot_primary
                        ActivityType.HOTEL -> R.drawable.circle_dot_teal
                        ActivityType.ATTRACTION -> R.drawable.circle_dot_purple
                        else -> R.drawable.circle_dot_grey
                    }
                    viewDot.setBackgroundResource(dotDrawable)

                    // Load activity or hotel image if provided
                    val imageUrl = if (act.image.isNotBlank()) act.image else (act.hotel?.image ?: "")
                    if (imageUrl.isNotEmpty() && ivActivityImage != null) {
                        ivActivityImage.visibility = View.VISIBLE
                        Glide.with(holder.itemView.context)
                            .load(imageUrl)
                            .placeholder(R.drawable.image11)
                            .error(R.drawable.image11)
                            .centerCrop()
                            .into(ivActivityImage)
                    } else {
                        ivActivityImage?.visibility = View.GONE
                    }

                    holder.llActivities.addView(activityView)
                }
            }
        }

        // Summary counts
        val totalActivities = dayItinerary.activities.size
        val byType = dayItinerary.activities.groupBy { it.type }
        val highlights = mutableListOf<String>()
        if (byType.containsKey(ActivityType.ATTRACTION)) highlights.add("Attractions")
        if (byType.containsKey(ActivityType.MEAL)) highlights.add("Food")
        if (byType.containsKey(ActivityType.HOTEL)) highlights.add("Stay")
        if (byType.containsKey(ActivityType.FLIGHT) || byType.containsKey(ActivityType.TRANSPORT)) highlights.add("Transport")

        holder.tvSummaryCounts.text = "Activities: $totalActivities"

        // Add tag chips
        val context = holder.itemView.context
        highlights.forEach { tag ->
            val tv = TextView(context)
            tv.text = tag
            tv.setTextColor(context.getColor(R.color.text_primary))
            tv.textSize = 12f
            tv.setBackgroundResource(R.drawable.chip_tag_bg)
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(0, 0, 8, 0)
            tv.layoutParams = params
            holder.llTags.addView(tv)
        }
    }

    override fun getItemCount() = dayItineraries.size
}