package br.com.myself.domain.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import br.com.myself.domain.database.MyDatabase
import br.com.myself.domain.entity.Entrada
import br.com.myself.util.Async

class EntradaRepository(application: Application) {
    
    private val dao = MyDatabase.getInstance(application).getEntradaDAO()
    
    fun pesquisarEntradas(ano: Int): PagingSource<Int, Entrada> {
        // Seguindo o pattern "yyyy-MM-dd"
        return dao.findAllByYear("$ano%")
    }
    
    fun delete(entrada: Entrada, onComplete: () -> Any) {
        Async.doInBackground({ dao.delete(entrada) }, { onComplete() })
    }
    
    fun salvar(entrada: Entrada, onComplete: () -> Any) {
        Async.doInBackground({ dao.persist(entrada) }, { onComplete() })
    }
    
    fun count(ano: Int): LiveData<Int> {
        return dao.countByYear("$ano%")
    }
    
    
}