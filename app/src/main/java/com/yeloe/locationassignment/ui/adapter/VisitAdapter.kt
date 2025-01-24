package com.yeloe.locationassignment.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yeloe.locationassignment.R
import com.yeloe.locationassignment.model.Visit

class VisitAdapter(private val visits: List<Visit>) :
    RecyclerView.Adapter<VisitAdapter.VisitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_visit, parent, false)
        return VisitViewHolder(view)
    }

    override fun onBindViewHolder(holder: VisitViewHolder, position: Int) {
        val visit = visits[position]
        holder.bind(visit)
    }

    override fun getItemCount() = visits.size

    class VisitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(visit: Visit) {
            "Location Name: ${visit.locationName}".also {
                itemView.findViewById<TextView>(R.id.locationName).text = it
            }
            "Date: ${visit.date}".also {
                itemView.findViewById<TextView>(R.id.date).text = it
            }
            "Entry Time: ${visit.entryTime}".also {
                itemView.findViewById<TextView>(R.id.entryTime).text = it
            }
            "Exit Time: ${visit.exitTime}".also {
                itemView.findViewById<TextView>(R.id.exitTime).text = it
            }
            "Duration: ${visit.duration}".also {
                itemView.findViewById<TextView>(R.id.duration).text = it
            }
        }
    }
}
