package com.francis.week6.models

import android.content.Context
import android.graphics.Color
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

object ContactStore : ViewModel() {
    /**
     * Get a reference to the database with path contacts.
     */
    private val dbContact = FirebaseDatabase.getInstance().getReference("contacts")

//    private val

    /**
     * immutable list of liveData contacts queried from firebase
     */
    private val _contacts = MutableLiveData<List<Contact>>()

    /**
     * mutable list of liveData contacts cbjects from firebase.
     */
    val contacts: LiveData<List<Contact>>
        get() = _contacts

    /**
     * Immutable list of liveData contact object from firebase.
     */
    private val _contact = MutableLiveData<Contact?>()

    /**
     * mutable list of liveData contacts cbjects from firebase.
     */
    val contact: LiveData<Contact?>
        get() = _contact

    /**
     * Immutable list of liveData result query from firebase.
     */
    private val _result = MutableLiveData<Exception?>()

    /**
     * mutable list of liveData result query from firebase.
     */
    val result: LiveData<Exception?>
        get() = _result

    private var _phoneContacts = MutableLiveData<List<Contact>>(listOf())
    val phoneContacts: MutableLiveData<List<Contact>>
        get() = _phoneContacts


    /**
     * Create an instance variable of ChildEventListener
     * that loads each data when item changes in the database.
     */
    private val childEventListener = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) {}

        override fun onChildMoved(snapshot: DataSnapshot, p1: String?) {}

        //Called when database entry is changed
        override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
            val contact = snapshot.getValue(Contact::class.java)
            contact?.id = snapshot.key
            _contact.value = contact
        }

        //called when an entry is added to the database
        override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
            val contact = snapshot.getValue(Contact::class.java)
            contact?.id = snapshot.key
            _contact.value = contact
        }

        //        Triggered when an entry is removed from the database
        override fun onChildRemoved(snapshot: DataSnapshot) {
            val contact = snapshot.getValue(Contact::class.java)
            contact?.id = snapshot.key
            contact?.isDeleted = true
            _contact.value = contact
        }
    }

    /**
     * assigns childEventListener to firebase reference.
     */
    fun getRealtimeUpdates() {
        dbContact.addChildEventListener(childEventListener)
    }

    /**
     *Add a contact to the database.
     */
    fun addContact(contact: Contact) {
        contact.id = dbContact.push().key //Firebase generate unique key.

        val ref = dbContact.child(contact.id!!) //Create a reference that points to the unique key.

        //Create a hash map to hold contact details
        val data = mutableMapOf<String, Any>()
        data["fullName"] = contact.fullName!!
        data["phone"] = contact.phone!!
        data["email"] = contact.email!!
        data["color"] = contact.color

        //Update firebase database with the hashmap data setting it to the key reference.
        ref.setValue(data).addOnCompleteListener {
            if (it.isSuccessful) {
                _result.value = null
            } else {
                _result.value = it.exception
            }
        }
    }

    /**
     * Laad all contacts entries saved on the database.
     */
    fun fetchContacts() {
        dbContact.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                _contacts.value = mutableListOf()
                Log.d("Network Call", "${error.code}")
            }

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

    /**
     * @param contact contact to update
     * Update the firebase database with the updated contact details.
     */
    fun updateContact(contact: Contact) {
        dbContact.child(contact.id!!).setValue(contact)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _result.value = null
                } else {
                    _result.value = it.exception
                }
            }
    }

    /**
     * @param id id of the contact to delete
     * Delete a contact from the firebase database given the id
     */
    fun deleteContact(id: String) {
        dbContact.child(id).setValue(null)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _result.value = null
                } else {
                    _result.value = it.exception
                }
            }
    }

    /**
     * Remove childEventListener when view model is removed
     */
    override fun onCleared() {
        super.onCleared()
        dbContact.removeEventListener(childEventListener)
    }

    /**
     * Getting data from phone contacts
     */
    fun getPhoneContacts(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val contactList = arrayListOf<Contact>() //declare array list to store phone contact.
            val resolver =
                context.contentResolver // declare a content resolver to query phone contact
            val contacts = resolver.query( //Query phone contacts and store in contacts variable
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null
            )
            if (contacts != null) { //Check to see if contact is null else loop to get relevant data.
                while (contacts.moveToNext()) {
                    val name =
                        contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val number =
                        contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val obj = Contact()
                    obj.fullName = name
                    obj.phone = number
                    obj.color = Color.argb(
                        255,
                        Random.nextInt(256),
                        Random.nextInt(256),
                        Random.nextInt(256)
                    )
                    contactList.add(obj)
                }
            }
            contacts?.close()
            _phoneContacts.postValue(contactList)
        }
    }

}