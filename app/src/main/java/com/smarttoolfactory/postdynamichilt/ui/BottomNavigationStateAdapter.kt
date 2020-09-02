package com.smarttoolfactory.postdynamichilt.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commitNow
import androidx.lifecycle.Lifecycle
import androidx.navigation.dynamicfeatures.fragment.DynamicNavHostFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter.FragmentTransactionCallback.OnPostEventListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.smarttoolfactory.core.ui.fragment.navhost.BaseDynamicNavHostFragment
import com.smarttoolfactory.core.ui.fragment.navhost.BaseNavHostFragment
import com.smarttoolfactory.postdynamichilt.R

/**
 * ViewPager2 Adapter for changing tabs of BottomNavigationView
 *
 * * This adapter is used because [BottomNavigationView] with back navigation does not support
 *  [DynamicNavHostFragment] since [NavHostFragment.create] returns [NavHostFragment]
 * instead of type T:[NavHostFragment]`
 */
class BottomNavigationStateAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    init {

        // Add a FragmentTransactionCallback to handle changing
        // the primary navigation fragment
        registerFragmentTransactionCallback(object : FragmentTransactionCallback() {

            override fun onFragmentMaxLifecyclePreUpdated(
                fragment: Fragment,
                maxLifecycleState: Lifecycle.State
            ) = if (maxLifecycleState == Lifecycle.State.RESUMED) {

                // This fragment is becoming the active Fragment - set it to
                // the primary navigation fragment in the OnPostEventListener
                OnPostEventListener {
                    fragment.parentFragmentManager.commitNow {
                        setPrimaryNavigationFragment(fragment)
                    }
                }
            } else {
                super.onFragmentMaxLifecyclePreUpdated(fragment, maxLifecycleState)
            }
        })
    }

    override fun getItemCount(): Int = 4

//    override fun createFragment(position: Int): Fragment {
//
//        return when (position) {
//            // Home(ViewPager Fragment NavHost Container)
//            0 -> NavHostContainerFragment.createNavHostContainerFragment(
//                R.layout.fragment_navhost_home,
//                R.id.nested_nav_host_fragment_home
//            )
//
//            // Dashboard Fragment NavHost Container
//            1 -> NavHostContainerFragment.createNavHostContainerFragment(
//                R.layout.fragment_navhost_dashboard,
//                R.id.nested_nav_host_fragment_dashboard
//            )
//
//            // Notification Fragment NavHost Container
//            2 -> NavHostContainerFragment.createNavHostContainerFragment(
//                R.layout.fragment_navhost_notification,
//                R.id.nested_nav_host_fragment_notification
//            )
//
//            // Account Fragment NavHost Container
//            else -> NavHostContainerFragment.createNavHostContainerFragment(
//                R.layout.fragment_navhost_account,
//                R.id.nested_nav_host_fragment_account
//            )
//        }
//    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {

            // Home nav graph
            0 ->
                BaseNavHostFragment
                    .createNavHostFragment(R.navigation.nav_graph_home)

            // Dashboard nav graph
            1 ->
                BaseDynamicNavHostFragment
                    .createDynamicNavHostFragment(R.navigation.nav_graph_dashboard_start)

            // Notification nav graph
            2 ->
                BaseNavHostFragment
                    .createNavHostFragment(R.navigation.nav_graph_notification_start)

            // Account nav graph
            else ->
                BaseNavHostFragment
                    .createNavHostFragment(R.navigation.nav_graph_account_start)
        }
    }
}
