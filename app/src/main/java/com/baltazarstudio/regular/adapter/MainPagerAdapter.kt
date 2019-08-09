package com.baltazarstudio.regular.adapter

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MainPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
    val viewPages = ArrayList<Fragment>()
    val viewPagesTitle = ArrayList<String>()

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
