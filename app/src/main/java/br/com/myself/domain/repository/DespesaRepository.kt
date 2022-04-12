package br.com.myself.domain.repository

import android.content.Context
import br.com.myself.application.Application
import br.com.myself.domain.entity.Despesa

class DespesaRepository(context: Context) {
    
    private val dao = (context.applicationContext as Application).getDatabase().getDespesaDAO()
    
    fun getAllDespesas(): List<Despesa> {
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
