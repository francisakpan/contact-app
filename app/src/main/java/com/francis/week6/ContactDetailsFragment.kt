package com.francis.week6

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.francis.week6.models.Contact
import com.francis.week6.models.ContactStore
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class ContactDetailsFragment : Fragment() {

    private lateinit var contact: Contact
    private lateinit var fullName: String
    private lateinit var initials: String

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                view?.findNavController()?.popBackStack()
            }
            R.id.action_share -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_TEXT, contact.phone)
                startActivity(Intent.createChooser(intent, "Share contact"))
            }
            R.id.action_delete -> {
                val alert = AlertDialog.Builder(requireContext())
                alert.setTitle("Delete")
                alert.setMessage("Are you sure you want to delete contact?")
                alert.setPositiveButton("Yes"){ it, _ ->
                    ContactStore.deleteContact(contact)
                    it.dismiss()
                    view?.findNavController()?.popBackStack()
                }
                alert.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.contact_menu, menu)
        menu.findItem(R.id.action_save).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            val args = ContactDetailsFragmentArgs.fromBundle(it)
            contact = args.contactData
            fullName = contact.fullName?.trim().toString()
            initials = fullName.first().toUpperCase().toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_contact_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = view.findViewById(R.id.toolsBar)
        val context = (activity as AppCompatActivity?)!!
        context.setSupportActionBar(toolbar)

        view.findViewById<FloatingActionButton>(R.id.edit_button).apply {
            setOnClickListener {
                val action = ContactDetailsFragmentDirections
                    .actionContactDetailsFragmentToAddContactFragment("Edit", contact)
                view.findNavController().navigate(action)
            }
        }

        view.findViewById<CollapsingToolbarLayout>(R.id.tools_layout).apply {
            title = fullName
        }

        view.findViewById<CardView>(R.id.card_view).apply {
            setCardBackgroundColor(contact.color)
        }

        view.findViewById<TextView>(R.id.detail_screen_initials_text_view).apply {
            text = initials
        }

        view.findViewById<FrameLayout>(R.id.call_action).apply {
            setOnClickListener {
                if (Build.VERSION.SDK_INT > 22) {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.CALL_PHONE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissions(
                            arrayOf(Manifest.permission.CALL_PHONE),
                            101
                        )
                    }else {
                        makeCall()
                    }
                }
            }
        }

        view.findViewById<FrameLayout>(R.id.message_action).apply {
            setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("sms:" + contact.phone)
                context.startActivity(intent)
            }
        }

        view.findViewById<RecyclerView>(R.id.recycler_view_list_phone).apply {
            val data = arrayListOf<String>()
            contact.phone?.let { data.add(it) }
            if (contact.email?.isNotEmpty() == true) {
                data.add(contact.email!!)
            }
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = PhoneDetailsAdapter(data)
            addItemDecoration(
                DividerItemDecoration(
                    getContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        view.findViewById<FrameLayout>(R.id.email_action).apply {
            if (contact.email == null || contact.email!!.isBlank()) {
                visibility = View.GONE
                view.findViewById<View>(R.id.email_spacer).visibility = View.GONE
                return
            }

            setOnClickListener {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:" + contact.email)
                context.startActivity(Intent.createChooser(intent, "Send mail"))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall()
            }
        }
    }

    private fun makeCall() {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:" + contact.phone)
        requireContext().startActivity(intent)
    }

}