package br.com.myself.components

import android.content.Context
import android.util.AttributeSet
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
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
            setText(calendar.formattedDate())
        }
        
        setText(calendar.formattedDate())
        
        setFocusable(false)
        setCursorVisible(false)
        
    }
    
    fun getTime(): Calendar {
        return calendar
    }
    
    fun setTime(calendar: Calendar) {
        this.calendar.timeInMillis = calendar.timeInMillis
        setText(calendar.formattedDate())
    }
    
    fun showCalendar(fragmentManager: FragmentManager, tag: String?) {
        mDatePicker.show(fragmentManager, tag)
    }
    
    fun showCalendar(fragmentTransaction: FragmentTransaction, tag: String?) {
        mDatePicker.show(fragmentTransaction, tag)
    }
    
}