package br.com.myself.viewmodel

import androidx.lifecycle.*
import androidx.paging.*
import br.com.myself.repository.EntradaRepository
import br.com.myself.ui.adapter.state.UIModelState
import br.com.myself.util.Utils
import java.util.*
import java.util.Calendar.YEAR

class EntradaViewModel(repository: EntradaRepository) : ViewModel() {
    
    private val _eventStream = MutableLiveData<Events>()
    private val _year = MutableLiveData(Utils.getCalendar().get(YEAR))
    
    val totalCount: LiveData<Int> = Transformations.switchMap(_year) { repository.count(it) }
    val isEmpty: LiveData<Boolean> get() = Transformations.map(totalCount) { it == 0 }
    val entradaEventsLiveData: LiveData<Events> get() = _eventStream
    
    val entradas: LiveData<PagingData<UIModelState>> = Transformations.switchMap(_year) {
        repository.pesquisarEntradas(it).map { pagingData ->
            pagingData.map { entrada -> UIModelState.UIEntradaState(entrada) }
                .insertSeparators { before, after ->
                    if (before == null) { /* Beginning of the list */
                        if (after == null) { /* list is empty */
                            return@insertSeparators null
                        }
                        return@insertSeparators UIModelState.SeparatorEntrada(after.entrada.data[Calendar.MONTH])
                    }
                    
                    if (after == null) /* end of the list */ return@insertSeparators null
                    
                    if (before.entrada.data[Calendar.MONTH] != after.entrada.data[Calendar.MONTH]) {
                        return@insertSeparators UIModelState.SeparatorEntrada(after.entrada.data[Calendar.MONTH])
                    }
                    return@insertSeparators null
                }
        }.cachedIn(viewModelScope)
    }
    
    fun getYear() = _year.value!!
    
    fun voltarAno() {
        _year.value = _year.value?.minus(1)
    }
    
    fun avancarAno() {
        _year.value = _year.value?.plus(1)
    }
    
    fun mostrarDetalhes(itemId: Long) {
        _eventStream.postValue(Events.ShowCardDetails(itemId))
    }
    
    fun fecharDetalhesEntrada() {
        _eventStream.postValue(Events.HideCardDetails)
    }
    
    
    sealed class Events {
        object HideCardDetails : Events()
        class ShowCardDetails(val itemId: Long): Events()
        class Message(val message: String?) : Events()
    }
    
    @Suppress("UNCHECKED_CAST")
    class Factory(private val repository: EntradaRepository): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EntradaViewModel(repository) as T
        }
    }
    
}
