package br.com.myself.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import br.com.myself.data.dto.EntradaDTO
import br.com.myself.data.dao.EntradaDAO
import br.com.myself.data.model.Entrada
import br.com.myself.data.api.utils.BackendError
import br.com.myself.data.api.EntradaAPI
import br.com.myself.util.DEFAULT_REQUEST_LOAD_SIZE
import br.com.myself.util.Utils.Companion.formattedDate

class EntradaRepository(
    private val dao: EntradaDAO,
    private val api: EntradaAPI,
) {
    
    
    fun pesquisarEntradas(ano: Int): LiveData<PagingData<Entrada>> {
        val pagingSourceFactory = { dao.findAllByYear("$ano%") }
        
        return Pager(
            config = PagingConfig(pageSize = DEFAULT_REQUEST_LOAD_SIZE,
                initialLoadSize = 10,
                enablePlaceholders = false),
            pagingSourceFactory = pagingSourceFactory
        ).liveData
    }
    
    suspend fun salvar(entrada: Entrada) {
        var synchronized = false
        try {
            val dto = EntradaDTO(
                objectID = entrada.objectID,
                descricao = entrada.descricao,
                valor = entrada.valor,
                data = entrada.data.formattedDate("yyyy-MM-dd")
            )
            api.insertOrUpdate(dto)
            synchronized = true
        } catch (e: BackendError) {
            Log.d("EntradaRepository | salvar(Entrada)", "Error Response :: ${e.message}")
            throw e
        } finally {
            Log.d("EntradaRepository | salvar(Entrada)", "Entrada salva! --> $entrada")
            dao.persist(entrada.apply { isSynchronized = synchronized })
        }
    }
    
    suspend fun delete(entrada: Entrada) {
        try {
            val response = api.deleteById(entrada.objectID)
            if (response.isSuccessful)
                dao.delete(entrada)
            Log.d("EntradaRepository | delete(Entrada)", "Entrada removida! --> $entrada")
        } catch (e: BackendError) {
            Log.d("EntradaRepository | delete(Entrada)", "Error Response :: ${e.message}")
            dao.persist(entrada.apply { isDeleted = true })
            throw e
        }
    }
    
    fun count(ano: Int): LiveData<Int> {
        return dao.countByYear("$ano%")
    }
    
    fun getEntradaById(itemId: Long): LiveData<Entrada> {
        return dao.findById(itemId)
    }
}
