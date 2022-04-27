package br.com.myself.repository

import android.app.Application
import androidx.lifecycle.LiveData
import br.com.myself.model.dao.CriseDAO
import br.com.myself.model.database.LocalDatabase
import br.com.myself.model.entity.Crise

class CriseRepository(application: Application) {
    private val dao: CriseDAO =
        LocalDatabase.getInstance(application).getCriseDAO()
    
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
