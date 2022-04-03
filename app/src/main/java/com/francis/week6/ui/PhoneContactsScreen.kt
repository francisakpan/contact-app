package com.francis.week6.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.francis.week6.R
import com.francis.week6.adapter.PhoneContactsAdapter
import com.francis.week6.models.ContactStore

/**
 * A simple [Fragment] subclass.
 */
class PhoneContactsScreen : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var button: Button
    private val adapter = PhoneContactsAdapter { contact ->
        //Get fragment navigation action and navigate to next fragment.
        val action =
            PhoneContactsScreenDirections.actionPhoneContactScreenToContactDetailsFragment(contact, 1)
        findNavController().navigate(action)
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) ContactStore.getPhoneContacts(requireContext())
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_imp2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = "Phone book"
            show()
        }

        //Initialize recyclerView and set layout manager.
        recyclerView = view.findViewById(R.id.imp2_recyclerView)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        button = view.findViewById<Button>(R.id.btn_request_permission).apply {
            setOnClickListener {
                permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            button.visibility = View.VISIBLE
        } else {
            if (adapter.currentList.isNullOrEmpty()) {
                ContactStore.getPhoneContacts(requireContext())
            }
        }

        ContactStore.phoneContacts.observe(viewLifecycleOwner) { contacts ->
            if (!contacts.isNullOrEmpty()) {
                button.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.submitList(contacts)
            }
        }
    }
}