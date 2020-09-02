package com.smarttoolfactory.dashboard

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.smarttoolfactory.core.ui.fragment.DynamicNavigationFragment
import com.smarttoolfactory.dashboard.databinding.FragmentDashboardBinding

class DashboardFragment : DynamicNavigationFragment<FragmentDashboardBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_dashboard

    override fun bindViews() {
        println("")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(requireContext(), "Dashboard Fragment", Toast.LENGTH_SHORT).show()
    }
}
