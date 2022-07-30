package br.com.myself.viewmodel

import androidx.lifecycle.*
import br.com.myself.data.model.Registro
import br.com.myself.repository.RegistroRepository

class PesquisarRegistrosViewModel(private val registroRepository: RegistroRepository) :
    ViewModel() {
    
    private val _queryLiveData = MutableLiveData<String>()
    
    val resultCount: Int get() = resultadoBusca.value?.size ?: 0
    
    val resultadoBusca: LiveData<List<Registro>> =
        Transformations.switchMap(_queryLiveData) { registroRepository.pesquisarRegistros(it) }
    
    fun setBusca(busca: String) {
        _queryLiveData.value = busca
    }
    
    fun hasAnyResult(): Boolean {
        return resultadoBusca.value?.size == 0
    }
    
    
    class Factory(private val repo: RegistroRepository) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PesquisarRegistrosViewModel(repo) as T
        }
    }
    
}
