package com.example.travelwise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelwise.R
import com.example.travelwise.models.PopularPlace

class PopularPlacesAdapter(private val places: List<PopularPlace>) :
    RecyclerView.Adapter<PopularPlacesAdapter.PlaceViewHolder>() {

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.ivPlace)
        val tvName: TextView = itemView.findViewById(R.id.tvPlaceName)
        val tvRating: TextView = itemView.findViewById(R.id.tvPlaceRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_popular_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.imageView.setImageResource(place.imageRes)
        holder.tvName.text = place.name
        holder.tvRating.text = place.rating
    }

    override fun getItemCount() = places.size
}