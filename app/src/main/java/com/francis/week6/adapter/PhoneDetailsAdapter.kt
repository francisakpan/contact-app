package com.francis.week6.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.francis.week6.R
import com.francis.week6.models.inflate

class PhoneDetailsAdapter(private val details: ArrayList<String>): RecyclerView.Adapter<PhoneDetailsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.contact_details_phone_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.contact_details_item))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.apply {
            text = details[position]
            if (position == 0) {
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone,0,0,0)
            }else {
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email,0,0,0)
            }
            compoundDrawablePadding = 32
        }
    }

    override fun getItemCount(): Int = details.size
}