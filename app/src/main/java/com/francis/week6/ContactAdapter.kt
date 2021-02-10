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
    private val clickListener: OnItemClickListener
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    private var contacts = mutableListOf<Contact>()

    interface OnItemClickListener {
        fun contactClicked(view: View, contact: Contact)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var contact: Contact

        init {
            //Assign onclick listener to item view
            itemView.setOnClickListener { clickListener.contactClicked(itemView, contact) }
        }

        /**
         * @param contact contact entry to populate the recycler view with.
         */
        fun bind(contact: Contact) {
            this.contact = contact

            //Initialize circular card view and set the color
            itemView.findViewById<CardView>(R.id.cardView).apply {
                setCardBackgroundColor(contact.color)
            }

            //Initialize initials textview and set text
            itemView.findViewById<TextView>(R.id.initials_text_view).also {
                it.text = contact.fullName?.first()?.toUpperCase().toString()
            }

            //Initialize full name text view and set text
            itemView.findViewById<TextView>(R.id.name_text_view).also {
                it.text = contact.fullName
            }
        }
    }

    /**
     * @param contacts Lists of contact items to display in the recycler view.
     * Set contact items to recycler view and notify recyclerview of any data change event.
     */
    fun populateView(contacts: List<Contact>) {
        this.contacts = contacts as MutableList<Contact>
        notifyDataSetChanged()
    }

    /**
     * @param contact contact to add.
     * Adds, delete and update a new contact to list.
     */
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