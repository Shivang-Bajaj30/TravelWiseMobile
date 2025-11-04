package com.example.travelwise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelwise.R
import com.example.travelwise.models.Flight

class FlightAdapter(
    private val flights: List<Flight>,
    private val onItemClick: (Flight) -> Unit
) : RecyclerView.Adapter<FlightAdapter.FlightViewHolder>() {

    inner class FlightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAirline: TextView = itemView.findViewById(R.id.tvAirline)
        val tvFlightNumber: TextView = itemView.findViewById(R.id.tvFlightNumber)
        val tvFrom: TextView = itemView.findViewById(R.id.tvFrom)
        val tvTo: TextView = itemView.findViewById(R.id.tvTo)
        val tvDepartureTime: TextView = itemView.findViewById(R.id.tvDepartureTime)
        val tvArrivalTime: TextView = itemView.findViewById(R.id.tvArrivalTime)
        val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvStops: TextView = itemView.findViewById(R.id.tvStops)
        val tvAircraft: TextView = itemView.findViewById(R.id.tvAircraft)

        fun bind(flight: Flight) {
            tvAirline.text = flight.airline
            tvFlightNumber.text = flight.flightNumber
            tvFrom.text = flight.from
            tvTo.text = flight.to
            tvDepartureTime.text = flight.departureTime
            tvArrivalTime.text = flight.arrivalTime
            tvDuration.text = flight.duration
            tvPrice.text = "₹${flight.price.toInt()}"
            tvStops.text = if (flight.stops == 0) "Non-stop" else "${flight.stops} stop${if (flight.stops > 1) "s" else ""}"
            tvAircraft.text = flight.aircraft

            itemView.setOnClickListener {
                onItemClick(flight)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flight_card, parent, false)
        return FlightViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        holder.bind(flights[position])
    }

    override fun getItemCount(): Int = flights.size
}

