package com.example.travelwise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelwise.R
import com.example.travelwise.models.Destination
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.annotation.LayoutRes

class DestinationAdapter(
    private val destinations: List<Destination>,
    private val onItemClick: (Destination) -> Unit,
    @LayoutRes private val itemLayoutResId: Int = R.layout.item_destination_card
) : RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder>() {

    inner class DestinationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivDestination: ImageView = itemView.findViewById(R.id.ivDestination)
        val tvDestinationName: TextView = itemView.findViewById(R.id.tvDestinationName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val btnFavorite: FloatingActionButton = itemView.findViewById(R.id.btnFavorite)

        fun bind(destination: Destination) {
            tvDestinationName.text = destination.name
            tvLocation.text = destination.location
            tvPrice.text = "$${destination.price}/Day"
            tvRating.text = destination.rating.toString()

            // Load image directly from drawable resource
            ivDestination.setImageResource(destination.imageResource)

            // Handle favorite button
            updateFavoriteIcon(destination.isFavorite)
            btnFavorite.setOnClickListener {
                destination.isFavorite = !destination.isFavorite
                updateFavoriteIcon(destination.isFavorite)
            }

            // Handle item click
            itemView.setOnClickListener {
                onItemClick(destination)
            }
        }

        private fun updateFavoriteIcon(isFavorite: Boolean) {
            val iconRes = if (isFavorite) {
                R.drawable.ic_heart
            } else {
                R.drawable.ic_heart
            }
            btnFavorite.setImageResource(iconRes)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(itemLayoutResId, parent, false)
        return DestinationViewHolder(view)
    }

    override fun onBindViewHolder(holder: DestinationViewHolder, position: Int) {
        holder.bind(destinations[position])
    }

    override fun getItemCount(): Int = destinations.size
}
