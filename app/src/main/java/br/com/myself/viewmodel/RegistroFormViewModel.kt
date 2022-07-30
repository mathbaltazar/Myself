package br.com.myself.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import br.com.myself.data.model.Registro
import br.com.myself.repository.RegistroRepository
import br.com.myself.util.Utils
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*

class RegistroFormViewModel(
    private val repository: RegistroRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    
    private val _events = MutableLiveData<Event>()
    private val _userMessage= MutableLiveData<String>()
    
    val registro: LiveData<Registro> = MutableLiveData(savedStateHandle["registro"])
    
    val events: LiveData<Event> = _events
    val userMessage: LiveData<String> = _userMessage
    
    fun salvarRegistro(
        descricao: String,
        valor: String,
        outros: String,
        data: Calendar) = viewModelScope.launch {
    
        
        if (validate(descricao, valor)) {
    
            val registro = registro.value?.copy(
                descricao = descricao.trim(),
                valor = Utils.unformatCurrency(valor).toDouble(),
                outros = outros.ifBlank { null },
                data = data
            ) ?: Registro(
                descricao = descricao.trim(),
                valor = Utils.unformatCurrency(valor).toDouble(),
                outros = outros.ifBlank { null },
                data = data
            )
            
            repository.salvarRegistro(registro)
            _userMessage.postValue("Dados salvos!")
            navigateBack()
            
        }
        
    }
    
    private fun navigateBack() {
        _events.postValue(Event.NavigateBack)
    }
    
    sealed class Event {
        object NavigateBack : Event()
    }
    
    private fun validate(descricao: String, valor: String): Boolean {
        return if (descricao.isBlank()) {
            _userMessage.postValue("Campo Descrição vazio")
            false
        } else if (valor.isBlank() || Utils.unformatCurrency(valor).toBigDecimal() <= BigDecimal.ZERO) {
            _userMessage.postValue("Campo Valor inválido")
            false
        } else true
    }
    
    class Factory(private val repo: RegistroRepository) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return RegistroFormViewModel(repo, extras.createSavedStateHandle()) as T
        }
    }
}
