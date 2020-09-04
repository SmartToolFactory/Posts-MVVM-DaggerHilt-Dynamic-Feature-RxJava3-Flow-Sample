package com.smarttoolfactory.postdynamichilt.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.NavHostFragment
import com.smarttoolfactory.core.ui.adapter.NavigableFragmentStateAdapter
import com.smarttoolfactory.core.ui.fragment.navhost.BaseDynamicNavHostFragment
import com.smarttoolfactory.core.ui.fragment.navhost.BaseNavHostFragment
import com.smarttoolfactory.postdynamichilt.R

// FIXME NOT working, solve the issue with Dynamic Fragments returning containerId 0

/**
 * FragmentStateAdapter to add [BaseDynamicNavHostFragment]s without adding extra wrapper fragment.
 * This adapter uses [BaseDynamicNavHostFragment.createDynamicNavHostFragment] to create a
 * [NavHostFragment] using the navigation resource as parameter
 */
class DynamicFragmentStateAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    NavigableFragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {

            // Home nav graph
            0 ->
                BaseDynamicNavHostFragment
                    .createDynamicNavHostFragment(R.navigation.nav_graph_dfm_home_start)

            // Dashboard nav graph
            1 ->
                BaseDynamicNavHostFragment
                    .createDynamicNavHostFragment(R.navigation.nav_graph_dfm_dashboard_start)

            // Notification nav graph
            2 ->
                BaseNavHostFragment
                    .createNavHostFragment(R.navigation.nav_graph_dfm_notification_start)

            // Account nav graph
            else ->
                BaseNavHostFragment
                    .createNavHostFragment(R.navigation.nav_graph_dfm_account_start)
        }
    }
}
