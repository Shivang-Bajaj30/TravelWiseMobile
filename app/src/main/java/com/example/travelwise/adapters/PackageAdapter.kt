package com.example.travelwise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travelwise.R
import com.example.travelwise.models.TravelPackage

class PackageAdapter(
    private val packages: List<TravelPackage>,
    private val onItemClick: (TravelPackage) -> Unit
) : RecyclerView.Adapter<PackageAdapter.PackageViewHolder>() {

    inner class PackageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.ivPackageImage)
        val tvTitle: TextView = itemView.findViewById(R.id.tvPackageTitle)
        val tvDetails: TextView = itemView.findViewById(R.id.tvPackageDetails)
        val tvDate: TextView = itemView.findViewById(R.id.tvPackageDate)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPackagePrice)

        init {
            itemView.setOnClickListener {
                onItemClick(packages[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_package_card, parent, false)
        return PackageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        val packageItem = packages[position]
        
        holder.tvTitle.text = packageItem.title
        holder.tvDetails.text = "Flight: ${packageItem.flightClass} • Hotel: ${packageItem.hotelClass}"
        holder.tvDate.text = packageItem.startDate
        holder.tvPrice.text = "$${String.format("%.1f", packageItem.price).replace(".", ",")}"
        
        // Load image using Glide
        if (packageItem.imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(packageItem.imageUrl)
                .placeholder(R.drawable.image11) // Fallback image
                .error(R.drawable.image11)
                .centerCrop()
                .into(holder.ivImage)
        } else {
            holder.ivImage.setImageResource(R.drawable.image11)
        }
    }

    override fun getItemCount() = packages.size
}

