package com.smarttoolfactory.postdynamichilt.main

import android.os.Bundle
import android.view.View
import com.smarttoolfactory.core.ui.fragment.DynamicNavigationFragment
import com.smarttoolfactory.postdynamichilt.R
import com.smarttoolfactory.postdynamichilt.databinding.FragmentMainViewpager2Binding
import com.smarttoolfactory.postdynamichilt.ui.DynamicFragmentStateAdapter

class MainFragmentViewPager2 : DynamicNavigationFragment<FragmentMainViewpager2Binding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_main_viewpager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = dataBinding!!

        val viewPager2 = binding.viewPager

        // Cancel ViewPager swipe
        viewPager2.isUserInputEnabled = false

        // Set viewpager adapter
        viewPager2.adapter =
            DynamicFragmentStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
    }

    override fun bindViews() {
        println("")
    }

    override fun onDestroyView() {

        val viewPager2 = dataBinding?.viewPager

        /*
            Without setting ViewPager2 Adapter it causes memory leak

            https://stackoverflow.com/questions/62851425/viewpager2-inside-a-fragment-leaks-after-replacing-the-fragment-its-in-by-navig
         */
        viewPager2?.let {
            it.adapter = null
        }

        super.onDestroyView()
    }
}
