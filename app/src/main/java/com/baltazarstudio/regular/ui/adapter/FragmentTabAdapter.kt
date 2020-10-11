package com.baltazarstudio.regular.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class FragmentTabAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    
    private val tabFragments: ArrayList<Fragment> = arrayListOf()
    private val tabTitles: ArrayList<String> = arrayListOf()
    
    fun addFragment(fragment: Fragment, title: String) {
        tabFragments.add(fragment)
        tabTitles.add(title)
    }
    
    override fun getCount(): Int {
        return tabFragments.size
    }
    
    override fun getItem(position: Int): Fragment {
        return tabFragments[position]
    }
    
    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }
}