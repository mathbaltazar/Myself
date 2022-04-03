package br.com.myself.model.repository

import android.content.Context
import br.com.myself.application.Application
import br.com.myself.model.dao.DespesaDAO
import br.com.myself.model.dao.RegistroDAO
import br.com.myself.model.entity.Registro

class RegistroRepository(context: Context) {
    
    private val registroDAO: RegistroDAO =
        (context.applicationContext as Application).getDatabase().getRegistroDAO()
    
    /*TODO private val despesaDAO: DespesaDAO =
        (context.applicationContext as Application).getDatabase().getDespesaDAO()*/
    
    
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
        registroDAO.deleteById(registro.id!!)
    }
    
    fun pesquisarRegistros(despesaId: Long): List<Registro> {
        return registroDAO.findAllRegistrosByDespesa(despesaId)
    }
    
    fun pesquisarRegistros(pesquisa: String): List<Registro> {
        return registroDAO.findAllRegistrosByDescricao("%$pesquisa%") // Cláusula LIKE
    }
    
    
}