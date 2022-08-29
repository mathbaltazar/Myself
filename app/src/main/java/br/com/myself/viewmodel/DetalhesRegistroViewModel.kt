package br.com.myself.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import br.com.myself.data.model.Registro
import br.com.myself.repository.RegistroRepository
import br.com.myself.util.KEY_REGISTRO_ID
import kotlinx.coroutines.launch

class DetalhesRegistroViewModel(
    private val repository: RegistroRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    
    
    private val _events = MutableLiveData<Event>()
    private val registroId: Long = savedStateHandle[KEY_REGISTRO_ID]!!

    val registro: LiveData<Registro> = repository.getRegistroById(registroId)
    val eventStream: LiveData<Event> = _events
    
    fun onEditButtonClick() {
        _events.value = Event.OnEdit(registro.value!!)
    }
    
    fun onDeleteButtonClick() {
        _events.value = Event.OnDelete(registroId)
    }
    
    fun deleteRegistro() = viewModelScope.launch {
        repository.excluirRegistro(registro.value!!)
    }
    
    sealed class Event {
        data class OnEdit(val registro: Registro): Event()
        data class OnDelete(val registroId: Long): Event()
    }
    
    class Factory(private val repo: RegistroRepository) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return DetalhesRegistroViewModel(repo, extras.createSavedStateHandle()) as T
        }
    }
}
