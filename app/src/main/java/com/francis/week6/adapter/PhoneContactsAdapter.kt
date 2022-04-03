package com.francis.week6.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.francis.week6.R
import com.francis.week6.models.Contact
import com.francis.week6.models.inflate

class PhoneContactsAdapter(private val callback: (Contact) -> Unit) :
    ListAdapter<Contact, PhoneContactsAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(contact: Contact) {
            //Initialize circular card view and set the color
            itemView.findViewById<CardView>(R.id.cardView).apply {
                setCardBackgroundColor(contact.color)
            }

            //Initialize initials textview and set text
            itemView.findViewById<TextView>(R.id.initials_text_view).also {
                it.text = contact.fullName?.first()?.toUpperCase().toString()
            }

            //Initialize full name text view and set text
            itemView.findViewById<TextView>(R.id.imp2_name_text_view).also {
                it.text = contact.fullName
            }

            //Initialize phone text view and set text.
            itemView.findViewById<TextView>(R.id.imp2_phone_text_view).also {
                it.text = contact.phone
            }

            itemView.setOnClickListener { callback(contact) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.imp2_contact_item))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int = currentList.size

    class DiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }
    }
}