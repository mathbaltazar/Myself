package br.com.myself.repository

import android.app.Application
import androidx.lifecycle.LiveData
import br.com.myself.model.database.LocalDatabase
import br.com.myself.model.entity.Despesa

class DespesaRepository(application: Application) {
    
    private val dao = LocalDatabase.getInstance(application).getDespesaDAO()
    
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
