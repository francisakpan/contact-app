package com.francis.week6

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.francis.week6.models.Contact
import com.francis.week6.models.inflate

class Imp2Adapter(private val contacts: ArrayList<Contact>): RecyclerView.Adapter<Imp2Adapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(contact: Contact){
            itemView.findViewById<CardView>(R.id.cardView).apply {
                setCardBackgroundColor(contact.color)
            }

            itemView.findViewById<TextView>(R.id.initials_text_view).also {
                it.text = contact.fullName?.first()?.toUpperCase().toString()
            }

            itemView.findViewById<TextView>(R.id.imp2_name_text_view).also {
                it.text = contact.fullName
            }

            itemView.findViewById<TextView>(R.id.imp2_phone_text_view).also {
                it.text = contact.phone
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.imp2_contact_item))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount(): Int = contacts.size
}