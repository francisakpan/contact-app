package com.francis.week6.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.francis.week6.R
import com.francis.week6.models.Contact
import com.francis.week6.models.ContactStore
import com.francis.week6.utils.Validator
import com.francis.week6.utils.Validator.Companion.isValidEmail
import com.google.android.material.textfield.TextInputEditText
import kotlin.random.Random

/**
 * A simple [Fragment] subclass.
 */
class AddContactFragment : Fragment() {
    private lateinit var action: String
    private lateinit var contact: Contact
    private lateinit var name: TextInputEditText
    private lateinit var phone: TextInputEditText
    private lateinit var email: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
         * Get arguments passed from navigation component
         */
        arguments?.let { argument ->
            val bundle = AddContactFragmentArgs.fromBundle(argument)
            action = bundle.screen //Action is either Add or Edit

            //Get contact from bundle.
            //If contact is null set contact to a dummy contact with empty values.
            contact =
                bundle.contact ?: Contact(null, false, "", "", "")
        }
    }

    private fun setMenuItemClickListener(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                view?.findNavController()?.popBackStack() //Navigate back
            }

            R.id.action_save -> {
                val contact = Contact(
                    fullName = name.text.toString(),
                    phone = phone.text.toString(),
                    email = email.text.toString()
                )

                if (action == "Add") { //If add generate random color value and assign to contact color.
                    contact.color = Color.argb(
                        255,
                        Random.nextInt(256),
                        Random.nextInt(256),
                        Random.nextInt(256)
                    )
                }

                // If all edit text are empty close fragment without adding to firebase
                if (Validator.editTextValidate(name, phone, email)){
                    view?.findNavController()?.popBackStack()
                    return false
                }

                //Check if a valid email string is entered.
                if (!(email.text.toString().isValidEmail()) &&
                        email.text?.isBlank() != true){
                    email.error = "Invalid email"
                    return false
                }

                if (action == "Add") {
                    ContactStore.addContact(contact) //Add a contact entry to firebase database.
                }else {
                    contact.id = this.contact.id
                    contact.color = this.contact.color
                    ContactStore.updateContact(contact) //Update a contact given id on the firebase.
                }

                /*
                    Use ContactStore livedata from add method to observe if adding to database is
                    successful of not. If successful close the fragment, else show a toast with error
                    message.
                 */
                ContactStore.result.observe(viewLifecycleOwner) {
                    if (it == null) {
                        view?.findNavController()?.popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Error Saving contact", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolsBar(view: View) {
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        val toolbar = view.findViewById<Toolbar>(R.id.add_screen_tools_bar)
        toolbar.title = getString(R.string.add_fragment_action_bar_title, action)
        toolbar.inflateMenu(R.menu.contact_menu)
        toolbar.menu.findItem(R.id.action_delete).isVisible = false
        toolbar.menu.findItem(R.id.action_share).isVisible = false
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        toolbar.setOnMenuItemClickListener {
            setMenuItemClickListener(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_contact, container, false)
        setupToolsBar(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Save "save to" text view
        view.findViewById<TextView>(R.id.save_text_view).apply {
            text = if (action == "Add"){
                String.format(getString(R.string.save_to_action_text), "")
            }else {
                String.format(getString(R.string.save_to_action_text), "d")
            }
        }

        //Initialize full name text field
        view.findViewById<TextInputEditText>(R.id.name_edit_text).also {
            it.setText(contact.fullName)
            name = it
        }

        //Initialize phone text field
        view.findViewById<TextInputEditText>(R.id.phone_edit_text).also {
            it.setText(contact.phone)
            phone = it
        }

        //Initialize email text field
        view.findViewById<TextInputEditText>(R.id.email_edit_text).also {
            it.setText(contact.email)
            email = it
        }
    }
}