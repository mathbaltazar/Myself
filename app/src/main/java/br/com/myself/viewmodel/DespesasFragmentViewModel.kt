package br.com.myself.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import br.com.myself.domain.entity.Despesa
import br.com.myself.domain.entity.Registro
import br.com.myself.domain.repository.DespesaRepository
import br.com.myself.domain.repository.RegistroRepository
import br.com.myself.util.Async
import java.util.*

class DespesasFragmentViewModel(application: Application) : AndroidViewModel(application) {
    private val repository by lazy { DespesaRepository(application) }
    
    val despesas: LiveData<List<Despesa>> = repository.getAllDespesas()
    
    fun salvar(despesa: Despesa, onSaved: () -> Unit) {
        Async.doInBackground({ repository.salvar(despesa) }, { onSaved() })
    }
    
    fun excluir(despesa: Despesa, onDeleted: () -> Unit) {
        Async.doInBackground({ repository.excluir(despesa) }, { onDeleted() })
    }
    
    fun registrarDespesa(despesa: Despesa, valor: Double, data: Calendar, onRegistered: () -> Unit) {
        Async.doInBackground({
            RegistroRepository(getApplication()).salvarRegistro(Registro(
                descricao = despesa.nome,
                valor = valor,
                data = data,
                despesa_id = despesa.id
            ))
        },{ onRegistered() })
    }
    
    fun getSugestoes(despesa: Despesa, onComplete: (List<Double>) -> Unit) {
        Async.doInBackground({
            RegistroRepository(getApplication()).getValoresPelaDespesaId(despesa.id)
        }, { valores -> onComplete(valores) })
    }
    
}
