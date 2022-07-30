package br.com.myself.repository

import androidx.lifecycle.LiveData
import br.com.myself.data.api.RegistroAPI
import br.com.myself.data.dao.RegistroDAO
import br.com.myself.data.model.Registro

class RegistroRepository(private val registroDAO: RegistroDAO) {
    
    fun pesquisarRegistros(mes: Int, ano: Int): LiveData<List<Registro>> {
        // Seguindo o pattern "yyyy-MM-dd"
        var monthLike = "%-"
        if (mes < 10) monthLike += "0"
        monthLike += "${mes}-%"
        
        val yearLike = "$ano-%"
        
        return registroDAO.findAllRegistrosByData(monthLike, yearLike)
    }
    
    suspend fun salvarRegistro(registro: Registro) {
        registroDAO.persist(registro.apply { isSynchronized = false })
    }
    
    suspend fun excluirRegistro(registro: Registro) {
        registroDAO.persist(registro.apply { isDeleted = true })
    }
    
    fun getValoresPelaDespesaId(despesaId: Long): List<Double> {
        return registroDAO.findAllValorByDespesaId(despesaId)
    }
    
    fun pesquisarRegistros(pesquisa: String): LiveData<List<Registro>> {
        return registroDAO.findAllRegistrosByDescricao("%$pesquisa%")
    }
    
    fun getRegistrosDaDespesa(despesaId: Long): LiveData<List<Registro>> {
        return registroDAO.findAllRegistrosByDespesaId(despesaId)
    }
    
    fun getRegistroById(registroId: Long): LiveData<Registro> {
        return registroDAO.findById(registroId)
    }
    
}