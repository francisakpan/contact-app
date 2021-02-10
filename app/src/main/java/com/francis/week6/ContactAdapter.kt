package com.francis.week6

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.francis.week6.models.Contact
import com.francis.week6.models.inflate

class ContactAdapter(
//    private val contacts: ArrayList<Contact>,
    private val clickListener: OnItemClickListener
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    private var contacts = mutableListOf<Contact>()

    interface OnItemClickListener {
        fun contactClicked(view: View, contact: Contact)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var contact: Contact

        init {
            itemView.setOnClickListener {
                clickListener.contactClicked(itemView, contact)
            }
        }

        fun bind(contact: Contact) {
            this.contact = contact

            itemView.findViewById<CardView>(R.id.cardView).apply {
                setCardBackgroundColor(contact.color)
            }

            itemView.findViewById<TextView>(R.id.initials_text_view).also {
                it.text = contact.fullName?.first()?.toUpperCase().toString()
            }

            itemView.findViewById<TextView>(R.id.name_text_view).also {
                it.text = contact.fullName
            }
        }
    }

    fun populateView(contacts: List<Contact>) {
        this.contacts = contacts as MutableList<Contact>
        notifyDataSetChanged()
    }

    fun addContact(contact: Contact) {
        if (!contacts.contains(contact)) {
            this.contacts.add(contact)
        } else {
            val index = contacts.indexOf(contact)
            if (contact.isDeleted == true) {
                contacts.removeAt(index)
            } else {
                contacts[index] = contact
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent.inflate(R.layout.contact_item))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount(): Int = contacts.size
}