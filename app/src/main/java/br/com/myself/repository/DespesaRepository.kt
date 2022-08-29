package br.com.myself.repository

import android.app.Application
import androidx.lifecycle.LiveData
import br.com.myself.database.LocalDatabase
import br.com.myself.data.model.Despesa

class DespesaRepository(application: Application) {
    
    private val dao = LocalDatabase.getInstance(application).getDespesaDAO()
    
    fun getAllDespesas(): LiveData<List<Despesa>> {
        return dao.findAll()
    }

    fun excluir(despesa: Despesa) {
        dao.delete(despesa)
    }
    
    fun salvar(despesa: Despesa): Long {
        return dao.persist(despesa)
    }
    
}
