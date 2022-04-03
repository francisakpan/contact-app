package com.francis.week6.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.francis.week6.R
import com.francis.week6.adapter.ContactAdapter
import com.francis.week6.models.Contact
import com.francis.week6.models.ContactStore
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class ContactScreen : Fragment(), ContactAdapter.OnItemClickListener {

    //Initialize recycler view adapter.
    private val contactAdapter = ContactAdapter(this)

    private var loading = false
    private lateinit var loadingImage: ImageView
    private lateinit var recyclerView: RecyclerView
    private val isContactEmpty = contactAdapter.size == 0

    private fun showLoading() {
        loadingImage.visibility = View.VISIBLE
        recyclerView.visibility = View.INVISIBLE
    }

    private fun hideLoading() {
        loading = false
        loadingImage.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loading = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d(javaClass.name, "onCreateView")
        return inflater.inflate(R.layout.fragment_imp1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Contacts"
            show()
        }

        //On view created fetch contact from firebase database
        ContactStore.fetchContacts()

        //Set childEventListener for realtime updates
        ContactStore.getRealtimeUpdates()

        loadingImage = view.findViewById(R.id.loading_indicator)

        //Initialize recycler view and set layout manager and adapter.
        view.findViewById<RecyclerView>(R.id.home_screen_recyclerView).apply {
            layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
            adapter = contactAdapter
            recyclerView = this
        }

        //show loading indicator
        if (loading) showLoading()

        //Observe for events from liveData and populate recycler view accordingly.
        ContactStore.contacts.observe(viewLifecycleOwner) { contact ->
            contactAdapter.populateView(contact)
            if (loading) { //hide loading indicator on data received.
                hideLoading()
            }
        }

        //Observe for updated events on liveData and add changes to recycler view contact item
        ContactStore.contact.observe(viewLifecycleOwner) { contact ->
            if (contact != null) {
                contactAdapter.addContact(contact)
            }
        }

        //Initialize Floating action button and set Onclick Listener.
        view.findViewById<FloatingActionButton>(R.id.add_button).apply {
            setOnClickListener {
                //Get fragment navigation action and navigate to next fragment.
                val action = ContactScreenDirections.actionHomeScreenFragmentToAddContactFragment2()
                view.findNavController().navigate(action)
            }
        }
    }

    /**
     * Implement the contactClicked listener for recyclerview item click
     */
    override fun contactClicked(view: View, contact: Contact) {
        //Get fragment navigation action and navigate to next fragment.
        val action = ContactScreenDirections
            .actionHomeScreenFragmentToContactDetailsFragment(contact)
        view.findNavController().navigate(action)
    }

}