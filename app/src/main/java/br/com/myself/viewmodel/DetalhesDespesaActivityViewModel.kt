package br.com.myself.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.myself.domain.entity.Despesa
import br.com.myself.domain.entity.Registro
import br.com.myself.domain.repository.DespesaRepository
import br.com.myself.domain.repository.RegistroRepository
import br.com.myself.util.Async
import java.util.*

class DetalhesDespesaActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val registroRepository: RegistroRepository by lazy { RegistroRepository(getApplication()) }
    private val despesaRepository: DespesaRepository by lazy { DespesaRepository(application) }
    
    lateinit var despesa: Despesa
    lateinit var registrosDaDespesa: LiveData<List<Registro>>
    
    val despesaEdited: MutableLiveData<Boolean> = MutableLiveData(false)
    
    fun loadDespesa(id: Long, onLoaded: () -> Unit) {
        Async.doInBackground({ despesaRepository.getDespesa(id) }, {
            despesa = it
            registrosDaDespesa = registroRepository.getRegistrosDaDespesa(it.id)
            onLoaded()
        })
    }
    
    fun wasEdited() = despesaEdited.value ?: false
    
    fun excluirDespesa(onDeleted: () -> Unit) {
        Async.doInBackground({ despesaRepository.excluir(despesa) }) {
            onDeleted()
        }
    }
    
    fun excluirRegistro(registro: Registro, onDeleted: () -> Unit) {
        Async.doInBackground({
            registroRepository.excluirRegistro(registro)
        }, { onDeleted() })
    }
    
    fun getSugestoes(onComplete: (List<Double>) -> Unit) {
        Async.doInBackground({ registroRepository.getValoresPelaDespesaId(despesa.id) }, { valores ->
            onComplete(valores)
        })
    }
    
    fun registrar(valor: Double, data: Calendar, onRegistered: () -> Unit) {
        Async.doInBackground({
            registroRepository.salvarRegistro(
                Registro(
                    descricao = despesa.nome,
                    valor = valor,
                    data = data,
                    despesa_id = despesa.id
                )
            )
        },{ onRegistered() })
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
