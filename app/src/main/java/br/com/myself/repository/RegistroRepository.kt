package br.com.myself.repository

import android.app.Application
import androidx.lifecycle.LiveData
import br.com.myself.data.dao.RegistroDAO
import br.com.myself.database.LocalDatabase
import br.com.myself.data.model.Registro

class RegistroRepository(application: Application) {
    
    private val registroDAO: RegistroDAO = LocalDatabase.getInstance(application).getRegistroDAO()
    
    fun pesquisarRegistros(mes: Int, ano: Int): LiveData<List<Registro>> {
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
    
    fun getValoresPelaDespesaId(despesaId: Long): List<Double> {
        return registroDAO.findAllValorByDespesaId(despesaId)
    }
    
    fun pesquisarRegistros(pesquisa: String): LiveData<List<Registro>> {
        return registroDAO.findAllRegistrosByDescricao("%$pesquisa%")
    }
    
    fun getRegistrosDaDespesa(despesaId: Long): LiveData<List<Registro>> {
        return registroDAO.findAllRegistrosByDespesaId(despesaId)
    }
    
}