package com.francis.week6

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private val contactList = arrayListOf<Contact>()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_imp2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolsBar: Toolbar = view.findViewById(R.id.imp2_toolsBar)
        val context = (activity as AppCompatActivity?)!!
        context.setSupportActionBar(toolsBar)
        toolsBar.title = "Phone Book"

        recyclerView = view.findViewById(R.id.imp2_recyclerView)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        /**
         * Permission request
         */
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_CONTACTS),
                100
            )
        } else getContact()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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
        val contactList = arrayListOf<Contact>()
        val resolver = requireActivity().contentResolver
        val contacts = resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null
        )
        if (contacts != null) {
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
        recyclerView.adapter = Imp2Adapter(contactList)
        contacts?.close()
    }
}