package com.francis.week6.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.francis.week6.R
import com.francis.week6.adapter.PhoneDetailsAdapter
import com.francis.week6.models.Contact
import com.francis.week6.models.ContactStore
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class ContactDetailsFragment : Fragment() {

    private var screen: Int = 0
    private lateinit var contact: Contact
    private lateinit var fullName: String
    private lateinit var initials: String
    private lateinit var toolBar: CollapsingToolbarLayout
    private lateinit var initialsTextView: TextView
    private lateinit var recyclerView: RecyclerView

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                makeCall()
            }
        }

    private fun setMenuItemClickListener(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                view?.findNavController()?.popBackStack() //Navigate back from fragment.
            }
            R.id.action_share -> {
                //Create a share intent for the contact.
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_TEXT, "${contact.phone}")
                intent.type = "text/plain"
                startActivity(Intent.createChooser(intent, "Share this contact using:"))
            }
            R.id.action_delete -> {
                //Show alert dialog to confirm delete before deletion.
                val alert = AlertDialog.Builder(requireContext())
                alert.setTitle("Delete")
                alert.setMessage("Are you sure you want to delete contact?")
                alert.setPositiveButton("Yes") { it, _ ->
                    ContactStore.deleteContact(contact.id!!)
                    it.dismiss()
                    view?.findNavController()?.popBackStack()
                }
                alert.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Get arguments passed from navigation component
        arguments?.let {
            val args = ContactDetailsFragmentArgs.fromBundle(it)
            screen = args.screen
            contact = args.contact
            fullName = contact.fullName?.trim().toString()
            initials = fullName.first().uppercaseChar().toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contact_details, container, false)
        setupToolsBar(view)
        return view
    }

    private fun setupToolsBar(view: View) {
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        val toolbar = view.findViewById<Toolbar>(R.id.toolsBar)
        toolbar.inflateMenu(R.menu.contact_menu)
        toolbar.menu.findItem(R.id.action_save).isVisible = false
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        toolbar.setOnMenuItemClickListener {
            setMenuItemClickListener(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initialize edit button and setOnclick listener to it
        view.findViewById<FloatingActionButton>(R.id.edit_button).apply {
            setOnClickListener {
                //Get fragment navigation action and navigate to next fragment.
                val action = ContactDetailsFragmentDirections
                    .actionContactDetailsFragmentToAddContactFragment(
                        if (screen == 0) "Edit" else "Add",
                        contact
                    )
                view.findNavController().navigate(action)
            }
        }

        //Initialize toolbar layout and set title to full name.
        view.findViewById<CollapsingToolbarLayout>(R.id.tools_layout).apply {
            toolBar = this
            title = fullName
        }

        //Initialize circular card view and set background color
        view.findViewById<CardView>(R.id.card_view).apply {
            setCardBackgroundColor(contact.color)
        }

        //Initialize Initials text view and set text to the contact initials
        view.findViewById<TextView>(R.id.detail_screen_initials_text_view).apply {
            initialsTextView = this
            text = initials
        }

        //Initialize call button and set onClick Listener
        view.findViewById<FrameLayout>(R.id.call_action).apply {
            setOnClickListener {
                permissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
        }

        //Initialize message button and setOnclick listener.
        view.findViewById<FrameLayout>(R.id.message_action).apply {
            setOnClickListener {
                //Create intent to send message to a provider phone number.
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("sms:" + contact.phone)
                context.startActivity(intent)
            }
        }

        //Initialize recyclerView.
        view.findViewById<RecyclerView>(R.id.recycler_view_list_phone).apply {
            recyclerView = this
            val data = arrayListOf<String>()
            contact.phone?.let { data.add(it) }
            if (contact.email?.isNotEmpty() == true) {
                data.add(contact.email!!)
            }

            //Set layout manager for the recyclerview.
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = PhoneDetailsAdapter(data) // Set recycler view adapter

            //add item separator to recycler view items
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        //Initialize email button and setOnclick listener.
        view.findViewById<FrameLayout>(R.id.email_action).apply {
            //Check if contact has an email else remove email button from screen.
            if (contact.email == null || contact.email!!.isBlank()) {
                visibility = View.GONE
                view.findViewById<View>(R.id.email_spacer).visibility = View.GONE
                return
            }

            setOnClickListener {
                //Create intent to send email to the provider email address.
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:" + contact.email)
                context.startActivity(Intent.createChooser(intent, "Send mail"))
            }
        }

        ContactStore.contact.observe(viewLifecycleOwner) { contact ->

            if (this.contact == contact) {
                toolBar.title = contact.fullName
                initialsTextView.text =
                    contact.fullName?.trim()?.uppercase(Locale.getDefault())?.first().toString()
                val data = arrayListOf<String>()
                contact.phone?.let { data.add(it) }
                if (contact.email?.isNotEmpty() == true) {
                    data.add(contact.email!!)
                }
                recyclerView.adapter = PhoneDetailsAdapter(data)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // If request code is correct. Make call.
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall()
            }
        }
    }

    /**
     * Create an intent to make a phone call.
     */
    private fun makeCall() {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:" + contact.phone)
        requireContext().startActivity(intent)
    }

}