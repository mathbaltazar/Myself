package com.baltazarstudio.regular.adapter

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class MainPagerAdapter(var viewPages: ArrayList<View>) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return viewPages.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = viewPages[position]
        container.addView(view)
        return view
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return viewPages[position].tag as String
    }

}
