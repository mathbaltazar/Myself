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

class CalendarPickerEditText(context: Context, attrs: AttributeSet) :
    TextInputEditText(context, attrs) {
    
    private lateinit var datePicker: MaterialDatePicker<Long>
    private val builder: MaterialDatePicker.Builder<Long>
    
    init {
        val calendarConstraint = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
                .setEnd(MaterialDatePicker.thisMonthInUtcMilliseconds())
                .build()
        
        builder = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(calendarConstraint)
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setTheme(R.style.CalendarPickerLayoutTheme)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        
        buildPicker()
    
        setText(datePicker.selection?.formattedDate())
    }
    
    private fun buildPicker() {
        datePicker = builder.build()
    
        datePicker.addOnPositiveButtonClickListener {
            setText(it.formattedDate())
        }
    }
    
    fun getTime(): Long {
        return datePicker.selection!!
    }
    
    fun setTime(time: Long) {
        builder.setSelection(time)
        buildPicker()
        setText(time.formattedDate())
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