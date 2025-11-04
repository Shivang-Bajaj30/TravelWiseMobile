package com.example.travelwise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelwise.R
import com.example.travelwise.models.Car

class CarAdapter(
    private val cars: List<Car>,
    private val onItemClick: (Car) -> Unit
) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    inner class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCarName: TextView = itemView.findViewById(R.id.tvCarName)
        val tvType: TextView = itemView.findViewById(R.id.tvType)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val tvTransmission: TextView = itemView.findViewById(R.id.tvTransmission)
        val tvFeatures: TextView = itemView.findViewById(R.id.tvFeatures)

        fun bind(car: Car) {
            tvCarName.text = car.name
            tvType.text = car.type
            tvPrice.text = "₹${car.price.toInt()}/day"
            tvRating.text = "⭐ ${car.rating}"
            tvTransmission.text = car.transmission
            tvFeatures.text = car.features.joinToString(" • ")

            itemView.setOnClickListener {
                onItemClick(car)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_car_card, parent, false)
        return CarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bind(cars[position])
    }

    override fun getItemCount(): Int = cars.size
}

