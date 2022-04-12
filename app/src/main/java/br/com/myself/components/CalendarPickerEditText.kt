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
    
    private lateinit var datePicker: MaterialDatePicker<Long>
    private val calendar: Calendar = Calendar.getInstance()
    
    init {
        calendar.timeInMillis = MaterialDatePicker.todayInUtcMilliseconds()
        
        val calendarConstraint = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
                .setEnd(MaterialDatePicker.thisMonthInUtcMilliseconds())
                .build()
        
        val builder = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(calendarConstraint)
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setTheme(R.style.CalendarPickerLayoutTheme)
            .setSelection(calendar.timeInMillis)
            .build()
        
        builder.addOnPositiveButtonClickListener {
            calendar.timeInMillis = it
            setText(calendar.formattedDate())
        }
        
        setText(calendar.formattedDate())
    }
    
    fun getTime(): Calendar {
        return calendar
    }
    
    fun setTime(calendar: Calendar) {
        this.calendar.timeInMillis = calendar.timeInMillis
        setText(calendar.formattedDate())
    }
    
    fun showCalendar(fragmentManager: FragmentManager, tag: String?) {
        datePicker.show(fragmentManager, tag)
    }
    
    fun showCalendar(fragmentTransaction: FragmentTransaction, tag: String?) {
        datePicker.show(fragmentTransaction, tag)
    }
    
    
    override fun setFocusable(focusable: Boolean) {
        // Focusable sempre FALSE
        super.setFocusable(false)
    }
    
    override fun setClickable(clickable: Boolean) {
        // Clickable sempre TRUE
        super.setClickable(true)
    }
    
}