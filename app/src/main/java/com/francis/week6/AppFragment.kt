package com.francis.week6

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController

/**
 * A simple [Fragment] subclass.
 */
class AppFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_app, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
            Initialize implementation 1 button and assign onClick Listener.
         */
        view.findViewById<Button>(R.id.imp_1).apply {
            setOnClickListener {
                // Get navigation component navigation action and navigate to new fragment
                val action = AppFragmentDirections.actionAppFragmentToHomeScreenFragment()
                view.findNavController().navigate(action)
            }
        }

        /*
            Initialize implementation 2 button and assign onClick Listener.
         */
        view.findViewById<Button>(R.id.imp_2).apply {
            setOnClickListener {
                // Get navigation component navigation action and navigate to new fragment
                val action = AppFragmentDirections.actionAppFragmentToImp2Fragment()
                view.findNavController().navigate(action)
            }
        }
    }

}