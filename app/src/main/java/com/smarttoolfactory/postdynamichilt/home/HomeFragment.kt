package com.smarttoolfactory.postdynamichilt.home

import com.smarttoolfactory.core.ui.fragment.DynamicNavigationFragment
import com.smarttoolfactory.postdynamichilt.R
import com.smarttoolfactory.postdynamichilt.databinding.FragmentHomeBinding

class HomeFragment : DynamicNavigationFragment<FragmentHomeBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_home
}
