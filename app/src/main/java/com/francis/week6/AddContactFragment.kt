package com.francis.week6

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.francis.week6.Validator.Companion.isValidEmail
import com.francis.week6.models.Contact
import com.francis.week6.models.ContactStore
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
            action = bundle.action //Action is either Add or Edit

            //Get contact from bundle.
            //If contact is null set contact to a dummy contact with empty values.
            contact =
                bundle.editContactData ?: Contact(null, false, "", "", "")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
                ContactStore.result.observe(viewLifecycleOwner,  {
                    if (it == null) {
                        view?.findNavController()?.popBackStack()
                    }else{
                        Toast.makeText(requireContext(), "Error Saving contact", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.contact_menu, menu)

        //Add fragment should not have delete and share menu items.
        menu.findItem(R.id.action_delete).isVisible = false
        menu.findItem(R.id.action_share).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_add_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = view.findViewById(R.id.add_screen_tools_bar)
        val context = (activity as AppCompatActivity?)!!
        context.setSupportActionBar(toolbar)

        //Set actionbar title
        if (action == "Add") {
            context.supportActionBar?.title = String.format(getString(R.string.add_fragment_action_bar_title), "Create")
        } else {
            context.supportActionBar?.title = String.format(getString(R.string.add_fragment_action_bar_title), "Edit")
        }

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