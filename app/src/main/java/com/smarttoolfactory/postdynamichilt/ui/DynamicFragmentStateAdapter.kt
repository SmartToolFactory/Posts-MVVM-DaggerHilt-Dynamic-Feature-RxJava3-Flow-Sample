package com.smarttoolfactory.postdynamichilt.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.smarttoolfactory.core.ui.adapter.NavigableFragmentStateAdapter
import com.smarttoolfactory.core.ui.fragment.navhost.BaseDynamicNavHostFragment
import com.smarttoolfactory.core.ui.fragment.navhost.BaseNavHostFragment
import com.smarttoolfactory.postdynamichilt.R

class DynamicFragmentStateAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    NavigableFragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {

            // Dashboard nav graph
            0 ->
                BaseDynamicNavHostFragment
                    .createDynamicNavHostFragment(R.navigation.nav_graph_home)

            // Notification nav graph
            1 ->
                BaseNavHostFragment
                    .createNavHostFragment(R.navigation.nav_graph_home)

            // Account nav graph
            else ->
                BaseNavHostFragment
                    .createNavHostFragment(R.navigation.nav_graph_home)
        }
    }
}
