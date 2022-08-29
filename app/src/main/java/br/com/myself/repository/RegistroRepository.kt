package br.com.myself.repository

import androidx.lifecycle.LiveData
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
        registro.apply {
            isSynchronized = false
            registroDAO.persist(this)
        }
    }
    
    suspend fun excluirRegistro(registro: Registro) {
        registro.apply {
            isDeleted = true
            registroDAO.persist(this)
        }
    }

    fun pesquisarRegistros(pesquisa: String): LiveData<List<Registro>> {
        return registroDAO.findAllRegistrosByDescricao("%$pesquisa%")
    }

    fun getRegistroById(registroId: Long): LiveData<Registro> {
        return registroDAO.findById(registroId)
    }
    
}