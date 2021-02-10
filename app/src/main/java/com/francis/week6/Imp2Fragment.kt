package com.francis.week6

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.francis.week6.models.Contact
import kotlin.random.Random

/**
 * A simple [Fragment] subclass.
 */
class Imp2Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var button: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_imp2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initialize and set toolbar.
        val toolsBar: Toolbar = view.findViewById(R.id.imp2_toolsBar)
        val context = (activity as AppCompatActivity?)!!
        context.setSupportActionBar(toolsBar)
        toolsBar.title = "Phone Book"

        //Initialize recyclerView and set layout manager.
        recyclerView = view.findViewById(R.id.imp2_recyclerView)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        view.findViewById<Button>(R.id.btn_request_permission).apply {
            button = this
            setOnClickListener {
                /**
                 * Permission request
                 */
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_CONTACTS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    //request for permission
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_CONTACTS),
                        100
                    )
                } else getContact()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //If request code is correct. display contact to screen.
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContact()
            }
        }
    }

    /**
     * Getting data from phone contacts
     */
    private fun getContact() {
        val contactList = arrayListOf<Contact>() //declare array list to store phone contact.
        val resolver =
            requireActivity().contentResolver // declare a content resolver to query phone contact
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
        button.visibility = View.INVISIBLE
        recyclerView.visibility = View.VISIBLE
        recyclerView.adapter = Imp2Adapter(contactList)
        contacts?.close()
    }
}