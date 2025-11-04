package com.example.travelwise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelwise.R
import com.example.travelwise.models.Hotel

class HotelAdapter(
    private val hotels: List<Hotel>,
    private val onItemClick: (Hotel) -> Unit
) : RecyclerView.Adapter<HotelAdapter.HotelViewHolder>() {

    inner class HotelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHotelName: TextView = itemView.findViewById(R.id.tvHotelName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val tvDistance: TextView = itemView.findViewById(R.id.tvDistance)
        val tvAmenities: TextView = itemView.findViewById(R.id.tvAmenities)

        fun bind(hotel: Hotel) {
            tvHotelName.text = hotel.name
            tvLocation.text = hotel.location
            tvPrice.text = "₹${hotel.price.toInt()}/night"
            tvRating.text = "⭐ ${hotel.rating}"
            tvDistance.text = hotel.distance
            tvAmenities.text = hotel.amenities.joinToString(" • ")

            itemView.setOnClickListener {
                onItemClick(hotel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hotel_card, parent, false)
        return HotelViewHolder(view)
    }

    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        holder.bind(hotels[position])
    }

    override fun getItemCount(): Int = hotels.size
}

