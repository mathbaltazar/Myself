package com.baltazarstudio.regular.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val viewPages = ArrayList<Fragment>()
    private val viewPagesTitle = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return viewPages[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        viewPages.add(fragment)
        viewPagesTitle.add(title)
    }

    override fun getCount(): Int {
        return viewPages.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return viewPagesTitle[position]
    }

}
