package com.smarttoolfactory.postdynamichilt.account

import com.smarttoolfactory.core.ui.fragment.DynamicNavigationFragment
import com.smarttoolfactory.postdynamichilt.R
import com.smarttoolfactory.postdynamichilt.databinding.FragmentAccountBinding

class AccountFragment : DynamicNavigationFragment<FragmentAccountBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_account

    override fun bindViews() {
        println("")
    }
}
