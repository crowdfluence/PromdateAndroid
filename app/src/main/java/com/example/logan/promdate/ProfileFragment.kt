package com.example.logan.promdate

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {

    val userId: ProfileFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set up toolbar at top of layout
        val appCompatActivity = activity as AppCompatActivity
        val toolbar: Toolbar = toolbar as Toolbar
        toolbar.title = getString(R.string.app_name)
        appCompatActivity.setSupportActionBar(toolbar)

        //add back button
        appCompatActivity.supportActionBar?.setDisplayShowHomeEnabled(true)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //load user data
        Toast.makeText(context, userId.toString(), Toast.LENGTH_SHORT).show()
        //TODO: Add call here
    }
}

