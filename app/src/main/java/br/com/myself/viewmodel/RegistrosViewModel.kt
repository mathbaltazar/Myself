package br.com.myself.viewmodel

import androidx.lifecycle.*
import br.com.myself.data.model.Registro
import br.com.myself.network.BackendNetworkIntegration
import br.com.myself.repository.RegistroRepository
import br.com.myself.util.Utils
import java.util.*

class RegistrosViewModel(private val repository: RegistroRepository) : ViewModel() {
    
    private val monthPageFilter: MonthPageFilter = MonthPageFilter()
    private val dateQueryLiveData = MutableLiveData(monthPageFilter)
    private val _pageFilterState =
        MutableLiveData<MonthPageFilterState>(MonthPageFilterState.LabelState)
    private val _eventsStream = MutableLiveData<Event>()
    
    val registros: LiveData<List<Registro>> =
        Transformations.switchMap(dateQueryLiveData) { filter ->
            repository.pesquisarRegistros(filter.month(), filter.year())
        }
    val totalMesAtualFormatado: String get() = Utils.formatCurrency(calcularTotal())
    val labelPageFormatado: String get() = "${Utils.MESES_STRING[monthPageFilter.month()]}/${monthPageFilter.year()}"
    val monthPageLayoutState: LiveData<MonthPageFilterState> get() = _pageFilterState
    val eventsStreamLiveData: LiveData<Event> get() = _eventsStream
    val backendNetworkIntegration = BackendNetworkIntegration()
    
    fun proximoMes() {
        dateQueryLiveData.value = monthPageFilter.proximoMes()
        _pageFilterState.value = MonthPageFilterState.LabelState
    }
    
    fun mesAnterior() {
        dateQueryLiveData.value = monthPageFilter.mesAnterior()
        _pageFilterState.value = MonthPageFilterState.LabelState
    }
    
    fun irParaData(month: Int = monthPageFilter.month(), year: Int = monthPageFilter.year()) {
        dateQueryLiveData.value = monthPageFilter.goTo(month = month, year = year)
    }
    
    private fun calcularTotal(): Double? {
        return registros.value?.sumOf(Registro::valor)
    }
    
    fun showJumpToDate() {
        _pageFilterState.value = MonthPageFilterState.DropdownState
    }
    
    fun mostrarDetalhes(registroId: Long) {
        _eventsStream.postValue(Event.NavigateToCardDetails(registroId))
    }
    
    
    private class MonthPageFilter(private val calendar: Calendar = Utils.getCalendar()) {
        fun proximoMes(): MonthPageFilter {
            calendar.add(Calendar.MONTH, 1)
            return this
        }
        
        fun mesAnterior(): MonthPageFilter {
            calendar.add(Calendar.MONTH, -1)
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
    
    sealed class MonthPageFilterState {
        object LabelState : MonthPageFilterState()
        object DropdownState : MonthPageFilterState()
    }
    
    sealed class Event {
        class NavigateToCardDetails(val id: Long) : Event()
    }
    
    class Factory(private val repo: RegistroRepository) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RegistrosViewModel(repo) as T
        }
    }
}