package com.smarttoolfactory.postdynamichilt.account

import com.smarttoolfactory.core.ui.fragment.DynamicNavigationFragment
import com.smarttoolfactory.postdynamichilt.R
import com.smarttoolfactory.postdynamichilt.databinding.FragmentDashboardBinding

class DashboardFragment : DynamicNavigationFragment<FragmentDashboardBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_dashboard

    override fun bindViews() {
        println("")
    }
}
