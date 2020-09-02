package com.smarttoolfactory.postdynamichilt.main

import android.os.Bundle
import android.view.View
import com.smarttoolfactory.core.ui.fragment.BaseDataBindingFragment
import com.smarttoolfactory.core.util.setupWithNavController
import com.smarttoolfactory.postdynamichilt.R
import com.smarttoolfactory.postdynamichilt.databinding.FragmentMainBottomNavBinding

class MainFragmentBottomNav : BaseDataBindingFragment<FragmentMainBottomNavBinding>() {
    override fun getLayoutRes(): Int = R.layout.fragment_main_bottom_nav

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {

        val bottomNavigationView = dataBinding!!.bottomNav

        val navGraphIds = listOf(
            R.navigation.nav_graph_home,
            R.navigation.nav_graph_dashboard_start,
            R.navigation.nav_graph_notification_start,
            R.navigation.nav_graph_account_start
        )

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = childFragmentManager,
            containerId = R.id.nav_host_container,
            intent = requireActivity().intent
        )
    }

    override fun bindViews() {
        println("")
    }
}
