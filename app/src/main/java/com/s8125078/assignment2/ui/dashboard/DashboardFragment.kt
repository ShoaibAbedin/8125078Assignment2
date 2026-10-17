package com.s8125078.assignment2.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.s8125078.assignment2.R
import dagger.hilt.android.AndroidEntryPoint

// TODO: show the entity list - for now just checking the keypass comes through
@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private val args: DashboardFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.tvSignedIn).text =
            getString(R.string.dashboard_signed_in, args.keypass)
    }
}
