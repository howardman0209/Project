package com.howard.project.ui.view

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.howard.project.R
import com.howard.project.databinding.ActivityLandingBinding
import com.howard.project.ui.base.MVVMActivity
import com.howard.project.ui.viewModel.LandingViewModel

class LandingActivity : MVVMActivity<LandingViewModel, ActivityLandingBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Log.d(LIFECYCLE, "LandingActivity onCreate")

        val navView: BottomNavigationView = binding.bottomNav

        navView.selectedItemId = R.id.bottom_nav_tab1
        navView.setOnItemSelectedListener {
            if (navView.selectedItemId == it.itemId) return@setOnItemSelectedListener false
            switchTab(it.itemId)
            true
        }
        switchTab(0)
    }

    private fun switchTab(itemId: Int) {
        val fragment = when (itemId) {
            R.id.bottom_nav_tab1 -> HomeFragment()
            R.id.bottom_nav_tab2 -> TestFragment()
            R.id.bottom_nav_tab3 -> TestFragment()
            R.id.bottom_nav_tab4 -> MoreFragment()
            else -> HomeFragment()
        }
        pushFragment(fragment, getMainFragmentContainer(), isAddToBackStack = false)
    }

    override fun getLayoutResId(): Int = R.layout.activity_landing

    override fun getViewModelInstance(): LandingViewModel = LandingViewModel()

    override fun getMainFragmentContainer(): Int =  R.id.baseActivityViewGroup

    override fun setBindingData() {
        binding.view = this
        binding.viewModel = viewModel
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount >= 1) {
            super.onBackPressed()
        } else {
            backToLogin()
        }
    }
}