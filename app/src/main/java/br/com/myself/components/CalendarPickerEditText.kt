package br.com.myself.components

import android.content.Context
import android.util.AttributeSet
import androidx.fragment.app.FragmentManager
import br.com.myself.R
import br.com.myself.util.Utils.Companion.formattedDate
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class CalendarPickerEditText(context: Context, attrs: AttributeSet) :
    TextInputEditText(context, attrs) {
    
    private val mDatePicker: MaterialDatePicker<Long>
    private val calendar: Calendar = Calendar.getInstance()
    private var mFragmentManager: FragmentManager? = null
    private var mDateSelectedListener: ((Calendar) -> Unit)? = null
    
    init {
        calendar.timeInMillis = MaterialDatePicker.todayInUtcMilliseconds()
        
        val calendarConstraint = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
                .setEnd(MaterialDatePicker.thisMonthInUtcMilliseconds())
                .build()
        
        mDatePicker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(calendarConstraint)
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setTheme(R.style.CalendarPickerLayoutTheme)
            .setSelection(calendar.timeInMillis)
            .build()
        
        mDatePicker.addOnPositiveButtonClickListener {
            calendar.timeInMillis = it
            setTime(calendar)
        }
        
        setText(calendar.formattedDate())
    
        isFocusable = false
        isCursorVisible = false
        
        setOnClickListener {
            mFragmentManager?.let { mDatePicker.show(it, null) }
        }
        
    }
    
    fun getTime(): Calendar {
        return calendar
    }
    
    fun setTime(calendar: Calendar) {
        this.calendar.timeInMillis = calendar.timeInMillis
        setText(calendar.formattedDate())
        mDateSelectedListener?.invoke(this.calendar)
    }
    
    fun bindFragmentManager(fragmentManager: FragmentManager) {
        this.mFragmentManager = fragmentManager
    }
    
    fun setOnDateSelected(listener: (Calendar) -> Unit) {
        this.mDateSelectedListener = listener
    }
    
    fun showCalendar(fragmentManager: FragmentManager, tag: String?) {
        mDatePicker.show(fragmentManager, tag)
    }
    
}