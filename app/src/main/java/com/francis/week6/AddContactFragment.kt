package com.francis.week6

import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.francis.week6.models.Contact
import com.francis.week6.models.ContactStore
import com.google.android.material.textfield.TextInputEditText
import org.w3c.dom.Text
import kotlin.random.Random

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class AddContactFragment : Fragment() {
    private lateinit var action: String
    private lateinit var contact: Contact
    private lateinit var name: TextInputEditText
    private lateinit var phone: TextInputEditText
    private lateinit var email: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { argument ->
            val bundle = AddContactFragmentArgs.fromBundle(argument)
            action = bundle.action
            contact =
                bundle.editContactData ?: Contact(null, false, "", "", "")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                view?.findNavController()?.popBackStack()
            }

            R.id.action_save -> {
                val contact = Contact(
                    fullName = name.text.toString(),
                    phone = phone.text.toString(),
                    email = email.text.toString()
                )

                if (action == "Add") {
                    contact.color = Color.argb(
                        255,
                        Random.nextInt(256),
                        Random.nextInt(256),
                        Random.nextInt(256)
                    )
                }

                if (action == "Add") {
                    ContactStore.addContact(contact)
                }else {
                    contact.id = this.contact.id
                    ContactStore.updateContact(contact)
                }

                view?.findNavController()?.popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.contact_menu, menu)
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
        ContactStore.result.observe(viewLifecycleOwner, Observer {
            val message = if (it == null) {
                "Successful"
            }else {
                "Error: " + it.message
            }
        })
        return inflater.inflate(R.layout.fragment_add_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = view.findViewById(R.id.add_screen_tools_bar)
        val context = (activity as AppCompatActivity?)!!
        context.setSupportActionBar(toolbar)

        if (action == "Add") {
            context.supportActionBar?.title = "Create contact"
        } else {
            context.supportActionBar?.title = "Edit contact"
        }

        view.findViewById<TextView>(R.id.save_text_view).apply {
            text = if (action == "Add"){
                "Save to "
            }else {
                "Saved to "
            }
        }

        view.findViewById<TextInputEditText>(R.id.name_edit_text).also {
            it.setText(contact.fullName)
            name = it
        }

        view.findViewById<TextInputEditText>(R.id.phone_edit_text).also {
            it.setText(contact.phone)
            phone = it
        }

        view.findViewById<TextInputEditText>(R.id.email_edit_text).also {
            it.setText(contact.email)
            email = it
        }
    }
}