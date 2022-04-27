package br.com.myself.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import br.com.myself.model.entity.Registro
import br.com.myself.repository.RegistroRepository
import br.com.myself.util.Async

class PesquisarRegistrosActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val registroRepository by lazy { RegistroRepository(application) }
    
    private val _queryLiveData = MutableLiveData<String>()
    
    val resultCount: Int get() = resultadoBusca.value?.size ?: 0
    
    val resultadoBusca: LiveData<List<Registro>>
        = Transformations.switchMap(_queryLiveData) { registroRepository.pesquisarRegistros(it) }
    
    fun excluir(registro: Registro, onDeleted: () -> Unit) {
        Async.doInBackground({ registroRepository.excluirRegistro(registro) },
            { onDeleted() })
    }
    
    fun salvar(novoregistro: Registro, onSaved: () -> Unit) {
        Async.doInBackground({ registroRepository.salvarRegistro(novoregistro) }, { onSaved() })
    }
    
    fun setBusca(busca: String) {
        _queryLiveData.value = busca
    }
    
    fun hasAnyResult(): Boolean {
        return resultadoBusca.value?.size == 0
    }
}
