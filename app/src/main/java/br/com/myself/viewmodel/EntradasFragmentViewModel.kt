package br.com.myself.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.map
import androidx.paging.*
import br.com.myself.domain.entity.Entrada
import br.com.myself.domain.repository.EntradaRepository
import br.com.myself.ui.adapter.EntradaAdapter
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.monthString
import java.util.*
import java.util.Calendar.YEAR

class EntradasFragmentViewModel(application: Application) : AndroidViewModel(application) {
    private val repository by lazy { EntradaRepository(application) }
    
    private val yearLiveData = MutableLiveData<Int>()
    val anoAtual: Int
        get() = yearLiveData.value ?: Utils.getCalendar().get(YEAR)
    
    private val pager = Pager(
        config = PagingConfig(12),
        pagingSourceFactory = {
            repository.pesquisarEntradas(anoAtual)
        }
    )
    val entradas: LiveData<PagingData<EntradaAdapter.UIModel>> =
        Transformations.switchMap(yearLiveData) { pager.liveData.map { pagingData ->
                pagingData
                    .map { entrada -> EntradaAdapter.UIModel.Item(entrada) }
                    .insertSeparators { before, after ->
                        if (before == null) { /* Beginning of the list */
                            if (after == null) { /* list is empty */
                                return@insertSeparators null
                            }
                            return@insertSeparators EntradaAdapter.UIModel.Separator(after.entrada.data.monthString())
                        }
                
                        if (after == null) /* end of the list */
                            return@insertSeparators null
                
                        if (before.entrada.data[Calendar.MONTH] != after.entrada.data[Calendar.MONTH]) {
                            return@insertSeparators EntradaAdapter.UIModel.Separator(after.entrada.data.monthString())
                        }
                        null
                    }
            }.cachedIn(viewModelScope) }
    val quantidadeEntradas = Transformations.switchMap(yearLiveData) { repository.count(anoAtual) }
    
    init {
        yearLiveData.value = anoAtual
    }
    
    fun excluir(entrada: Entrada, onComplete: () -> Any) {
        repository.delete(entrada, onComplete)
    }
    
    fun salvar(entrada: Entrada, onComplete: () -> Any) {
        repository.salvar(entrada, onComplete)
    }
    
    fun voltarAno() {
        yearLiveData.value = anoAtual - 1
    }
    
    fun avancarAno() {
        yearLiveData.value = anoAtual + 1
    }
}
