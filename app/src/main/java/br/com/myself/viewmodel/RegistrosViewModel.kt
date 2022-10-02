package br.com.myself.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.Observer
import br.com.myself.data.model.Registro
import br.com.myself.network.ExpenseIntegration
import br.com.myself.network.NetworkDataIntegration
import br.com.myself.repository.RegistroRepository
import br.com.myself.ui.financas.state.RegistrosFragmentUIState
import br.com.myself.ui.adapter.state.UIModelState
import br.com.myself.ui.financas.state.RegistrosFragmentBehaviorState
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.formattedDate
import kotlinx.coroutines.flow.*
import java.util.*

class RegistrosViewModel(private val repository: RegistroRepository) : ViewModel() {



    private val monthPageManager: MonthPageManager = MonthPageManager()
    private val pageManagerLiveData = MutableLiveData(monthPageManager)
    private val uiState = MutableStateFlow(RegistrosFragmentUIState.create())
    private val registrosLiveData: LiveData<List<Registro>> =
        Transformations.switchMap(pageManagerLiveData) {
            repository.pesquisarRegistros(monthPageManager.month(), monthPageManager.year())
        }
    private val _uiBehaviorState = MutableStateFlow(RegistrosFragmentBehaviorState.create())
    val uiBehaviorState: StateFlow<RegistrosFragmentBehaviorState> get() = _uiBehaviorState.asStateFlow()
    val expenseDataIntegration: NetworkDataIntegration = ExpenseIntegration()

    fun proximoMes() {
        pageManagerLiveData.value = monthPageManager.proximoMes()
    }
    
    fun mesAnterior() {
        pageManagerLiveData.value = monthPageManager.mesAnterior()
    }
    
    private fun mostrarDetalhes(id: Long) {
        _uiBehaviorState.update { it.copy(registroId = id) }
    }

    fun detailsShown() {
        _uiBehaviorState.update { it.copy(registroId = null) }
    }

    fun observeRegistroUIState(
        lifecycleOwner: LifecycleOwner,
        observer: (RegistrosFragmentUIState) -> Unit
    ) {
        registrosLiveData.observe(lifecycleOwner, Observer { listaRegistros ->
            observer(uiState.updateAndGet { it.copy(
                    labelMesAnoSelecionado = monthPageManager.getCurrentDate().formattedDate("MMMM/yyyy"),
                    quantidadeGastos = "${registrosLiveData.value!!.size}",
                    totalGastos = Utils.formatCurrency(listaRegistros.sumOf(Registro::valor)),
                    isEmpty = listaRegistros.isEmpty(),
                    registros = listaRegistros.map(::toRegistroItemUIState)
                )
            })
        })
    }

    private fun toRegistroItemUIState(registro: Registro): UIModelState {
        return UIModelState.UIRegistroState(
            id = registro.id!!,
            descricao = registro.descricao,
            valor = Utils.formatCurrency(registro.valor),
            data = registro.data.formattedDate(),
            outros = registro.outros,
            isSync = registro.isSynchronized,
            onItemSelected = {
                mostrarDetalhes(registro.id!!)
            }
        )
    }

    fun syncErrorShown() {
        this.expenseDataIntegration.updateState { it.copy(onError = null) }
    }


    private class MonthPageManager(private val calendar: Calendar = Utils.getCalendar()) {
        fun proximoMes(): MonthPageManager {
            calendar.add(Calendar.MONTH, 1)
            return this
        }
        
        fun mesAnterior(): MonthPageManager {
            calendar.add(Calendar.MONTH, -1)
            return this
        }
        
        fun month(): Int {
            return calendar[Calendar.MONTH]
        }
        
        fun year(): Int {
            return calendar[Calendar.YEAR]
        }

        fun getCurrentDate(): Calendar = calendar
    }
    
    class Factory(private val repo: RegistroRepository) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RegistrosViewModel(repo) as T
        }
    }
}