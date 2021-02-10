package com.francis.week6

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.francis.week6.models.Contact
import com.francis.week6.models.ContactStore
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class Imp1Fragment : Fragment(), ContactAdapter.OnItemClickListener {

    private val contactAdapter = ContactAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_imp1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ContactStore.fetchContacts()

        ContactStore.getRealtimeUpdates()

        view.findViewById<RecyclerView>(R.id.home_screen_recyclerView).apply {
            layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
            adapter = contactAdapter
        }

        ContactStore.contacts.observe(viewLifecycleOwner, { contact ->
            contactAdapter.populateView(contact)
        })

        ContactStore.contact.observe(viewLifecycleOwner, {contact ->
            contactAdapter.addContact(contact)
        })

        view.findViewById<FloatingActionButton>(R.id.add_button).apply {
            setOnClickListener {
                val action = Imp1FragmentDirections
                    .actionHomeScreenFragmentToAddContactFragment(editContactData = null)
                view.findNavController().navigate(action)
            }
        }
    }

    override fun contactClicked(view: View, contact: Contact) {
        val action = Imp1FragmentDirections
            .actionHomeScreenFragmentToContactDetailsFragment(contact)
        view.findNavController().navigate(action)
    }
}