package com.francis.week6.models

import android.content.Context
import android.util.Log
import android.util.LogPrinter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*

object ContactStore: ViewModel() {
    private val dbContact = FirebaseDatabase.getInstance().getReference("contacts")

    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>>
        get() = _contacts

    private val _contact = MutableLiveData<Contact>()
    val contact: LiveData<Contact>
        get() = _contact

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?>
        get() = _result

    private val childEventListener = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) {}

        override fun onChildMoved(snapshot: DataSnapshot, p1: String?) {}

        override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
            val contact = snapshot.getValue(Contact::class.java)
            contact?.id = snapshot.key
            _contact.value = contact
        }

        override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
            val contact = snapshot.getValue(Contact::class.java)
            contact?.id = snapshot.key
            _contact.value = contact
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val contact = snapshot.getValue(Contact::class.java)
            contact?.id = snapshot.key
            contact?.isDeleted = true
            _contact.value = contact
        }
    }

    fun getRealtimeUpdates() {
        dbContact.addChildEventListener(childEventListener)
    }

    fun addContact(contact: Contact) {
        contact.id = dbContact.push().key

        val ref = dbContact.child(contact.id!!)

        val data = mutableMapOf<String, Any>()
        data["fullName"] = contact.fullName!!
        data["phone"] = contact.phone!!
        data["email"] = contact.email!!
        data["color"] = contact.color

        ref.setValue(data).addOnCompleteListener {
            if (it.isSuccessful) {
                _result.value = null
            } else {
                _result.value = it.exception
            }
        }
    }

    fun fetchContacts() {
        dbContact.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val contacts = mutableListOf<Contact>()
                    for (contactSnapshot in snapshot.children) {
                        val contact = contactSnapshot.getValue(Contact::class.java)
                        contact?.id = contactSnapshot.key
                        contact?.let { contacts.add(it) }
                    }
                    _contacts.value = contacts
                }
            }
        })
    }

    fun updateContact(contact: Contact) {
        println(contact.id)
        contact.id?.let {id ->
            dbContact.child(id).setValue(contact)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        _result.value = null
                    } else {
                        _result.value = it.exception
                    }
                }
        }
    }

    fun deleteContact(contact: Contact) {
        contact.id?.let {id ->
            dbContact.child(id).setValue(null)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        _result.value = null
                    } else {
                        _result.value = it.exception
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        dbContact.removeEventListener(childEventListener)
    }

}