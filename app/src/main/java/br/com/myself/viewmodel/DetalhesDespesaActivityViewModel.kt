package br.com.myself.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.myself.data.model.Despesa
import br.com.myself.data.model.Registro
import br.com.myself.repository.DespesaRepository
import br.com.myself.util.Async
import kotlinx.coroutines.launch
import java.util.*

class DetalhesDespesaActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val despesaRepository: DespesaRepository by lazy { DespesaRepository(application) }
    
    lateinit var despesa: Despesa
    lateinit var registrosDaDespesa: LiveData<List<Registro>>
    
    val despesaEdited: MutableLiveData<Boolean> = MutableLiveData(false)
    
    fun attachDespesa(despesa: Despesa) {
        this.despesa = despesa
    }
    fun wasEdited() = despesaEdited.value ?: false
    
    fun excluirDespesa(onDeleted: () -> Unit) {
        Async.doInBackground({ despesaRepository.excluir(despesa) }) {
            onDeleted()
        }
    }

    fun registrar(valor: Double, data: Calendar, onRegistered: () -> Unit) {
        Registro(descricao = despesa.nome, valor = valor, data = data)
        // todo
    }
    
    fun salvarDespesa(onSaved: (() -> Unit)? = null) {
        Async.doInBackground({ despesaRepository.salvar(despesa) }) {
            despesaEdited.value = false
            onSaved?.invoke()
        }
    }
    
    fun setDespesaEdited() {
        despesaEdited.value = true
    }
    
}
