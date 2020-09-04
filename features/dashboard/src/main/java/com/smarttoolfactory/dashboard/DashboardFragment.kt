package com.smarttoolfactory.dashboard

import com.smarttoolfactory.core.ui.fragment.DynamicNavigationFragment
import com.smarttoolfactory.dashboard.databinding.FragmentDashboardBinding

class DashboardFragment : DynamicNavigationFragment<FragmentDashboardBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_dashboard
}
