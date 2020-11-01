package com.baltazarstudio.regular.components

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class LockableViewPager : ViewPager {
    
    var locked: Boolean = false
    
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (locked)
            return false
        return super.onTouchEvent(ev)
    }
    
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (locked)
            return false
        return super.onInterceptTouchEvent(ev)
    }
}