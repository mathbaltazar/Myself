package br.com.myself.domain.repository

import android.content.Context
import br.com.myself.application.Application
import br.com.myself.domain.dao.RegistroDAO
import br.com.myself.domain.entity.Registro

class RegistroRepository(context: Context) {
    
    private val registroDAO: RegistroDAO =
        (context.applicationContext as Application).getDatabase().getRegistroDAO()
    
    fun pesquisarRegistros(mes: Int, ano: Int): List<Registro> {
        // Seguindo o pattern "yyyy-MM-dd"
        var monthLike = "%-"
        if (mes < 10) monthLike += "0"
        monthLike += "${mes}-%"
        
        val yearLike = "$ano-%"
        
        return registroDAO.findAllRegistrosByData(monthLike, yearLike)
    }
    
    fun salvarRegistro(registro: Registro) {
        registroDAO.persist(registro)
    }
    
    fun excluirRegistro(registro: Registro) {
        registroDAO.delete(registro)
    }
    
    fun pesquisarRegistros(despesaId: Long): List<Registro> {
        return registroDAO.findAllRegistrosByDespesa(despesaId)
    }
    
    fun pesquisarRegistros(pesquisa: String): List<Registro> {
        return registroDAO.findAllRegistrosByDescricao("%$pesquisa%") // Cláusula LIKE
    }
    
    
}