package br.com.myself.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import br.com.myself.data.model.Registro
import br.com.myself.repository.RegistroRepository
import br.com.myself.util.Async
import br.com.myself.util.Utils
import java.util.*

class RegistrosFragmentViewModel(application: Application) : AndroidViewModel(application) {
    
    private val registroRepository by lazy { RegistroRepository(application) }
    private val dateQueryLiveData = MutableLiveData<MonthPageFilter>()
    private val monthPageFilter: MonthPageFilter = MonthPageFilter()
    
    val registros: LiveData<List<Registro>> =
        Transformations.switchMap(dateQueryLiveData) { filter ->
            registroRepository.pesquisarRegistros(filter.month(), filter.year())
        }
    val totalMesAtualFormatado: String get() = Utils.formatCurrency(calcularTotal())
    val labelPageFormatado: String get() = "${Utils.MESES_STRING[monthPageFilter.month()]}/${monthPageFilter.year()}"
    val month: Int get() = monthPageFilter.month()
    val year: Int get() = monthPageFilter.year()
    
    init {
        dateQueryLiveData.value = monthPageFilter
    }
    
    fun proximoMes() {
        dateQueryLiveData.value = monthPageFilter.proximoMes()
    }
    
    fun mesAnterior() {
        dateQueryLiveData.value = monthPageFilter.mesAnterior()
    }
    
    fun irParaData(month: Int = monthPageFilter.month(), year: Int = monthPageFilter.year()) {
        dateQueryLiveData.value = monthPageFilter.goTo(month = month, year = year)
    }
    
    private fun calcularTotal(): Double? {
        return registros.value?.sumOf(Registro::valor)
    }
    
    fun salvarRegistro(registro: Registro, onSaved: () -> Unit) {
        Async.doInBackground({
            registroRepository.salvarRegistro(registro)
        }, { onSaved() })
    }
    
    fun excluirRegistro(registro: Registro, onDeleted: () -> Unit) {
        Async.doInBackground({
            registroRepository.excluirRegistro(registro)
        }, { onDeleted() })
    }
    
    
    private class MonthPageFilter(private val calendar: Calendar = Utils.getCalendar()) {
        fun proximoMes(): MonthPageFilter {
            calendar.roll(Calendar.MONTH, true)
            return this
        }
        
        fun mesAnterior(): MonthPageFilter {
            calendar.roll(Calendar.MONTH, false)
            return this
        }
        
        fun month(): Int {
            return calendar[Calendar.MONTH]
        }
        
        fun year(): Int {
            return calendar[Calendar.YEAR]
        }
        
        fun goTo(month: Int = month(), year: Int = year()): MonthPageFilter {
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            return this
        }
    }
}