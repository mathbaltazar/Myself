package br.com.myself.domain.repository

import android.app.Application
import androidx.lifecycle.LiveData
import br.com.myself.domain.database.MyDatabase
import br.com.myself.domain.entity.Despesa

class DespesaRepository(application: Application) {
    
    private val dao = MyDatabase.getInstance(application).getDespesaDAO()
    
    fun getAllDespesas(): LiveData<List<Despesa>> {
        return dao.findAll()
    }
    
    fun getDespesa(id: Long): Despesa {
        return dao.find(id)
    }
    
    fun excluir(despesa: Despesa) {
        dao.delete(despesa)
    }
    
    fun salvar(despesa: Despesa): Long {
        return dao.persist(despesa)
    }
    
}
