package com.francis.week6

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import com.francis.week6.models.ContactStore

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        ContactStore.loadContacts(this)
        Navigation.findNavController(this, R.id.nav_host_fragment)

    }
}