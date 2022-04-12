package br.com.myself.domain.repository

import android.content.Context
import br.com.myself.application.Application
import br.com.myself.domain.dao.CriseDAO
import br.com.myself.domain.entity.Crise

class CriseRepository(context: Context) {
    private val dao: CriseDAO =
        (context.applicationContext as Application).getDatabase().getCriseDAO()
    
    fun salvar(crise: Crise): Long {
        return dao.persist(crise)
    }
    
    fun excluir(crise: Crise) {
        dao.delete(crise)
    }
    
    fun getTodasCrises(): List<Crise> {
        return dao.findAll()
    }
    
    
}
