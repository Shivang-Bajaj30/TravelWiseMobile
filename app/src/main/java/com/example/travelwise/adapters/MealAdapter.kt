package com.example.travelwise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelwise.R
import com.example.travelwise.models.Meal

class MealAdapter(
    private val meals: List<Meal>,
    private val onItemClick: (Meal) -> Unit
) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    inner class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMealName: TextView = itemView.findViewById(R.id.tvMealName)
        val tvRestaurant: TextView = itemView.findViewById(R.id.tvRestaurant)
        val tvCuisine: TextView = itemView.findViewById(R.id.tvCuisine)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)

        fun bind(meal: Meal) {
            tvMealName.text = meal.name
            tvRestaurant.text = meal.restaurant
            tvCuisine.text = meal.cuisine
            tvPrice.text = "₹${meal.price.toInt()}"
            tvRating.text = "⭐ ${meal.rating}"
            tvLocation.text = meal.location

            itemView.setOnClickListener {
                onItemClick(meal)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meal_card, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.bind(meals[position])
    }

    override fun getItemCount(): Int = meals.size
}

