package br.com.myself.domain.repository

import android.app.Application
import androidx.lifecycle.LiveData
import br.com.myself.domain.dao.CriseDAO
import br.com.myself.domain.database.MyDatabase
import br.com.myself.domain.entity.Crise

class CriseRepository(application: Application) {
    private val dao: CriseDAO =
        MyDatabase.getInstance(application).getCriseDAO()
    
    fun salvar(crise: Crise): Long {
        return dao.persist(crise)
    }
    
    fun excluir(crise: Crise) {
        dao.delete(crise)
    }
    
    fun getTodasCrises(): LiveData<List<Crise>> {
        return dao.findAll()
    }
    
}
