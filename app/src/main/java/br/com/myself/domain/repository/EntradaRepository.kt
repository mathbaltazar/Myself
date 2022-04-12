package br.com.myself.domain.repository

import android.content.Context
import br.com.myself.application.Application
import br.com.myself.domain.entity.Entrada

class EntradaRepository(val context: Context) {
    
    private val dao = (context.applicationContext as Application).getDatabase().getEntradaDAO()
    
    fun pesquisarEntradas(mes: Int, ano: Int): List<Entrada> {
        // Seguindo o pattern "yyyy-MM-dd"
        var monthLike = "%-"
        if (mes < 10) monthLike += "0"
        monthLike += "${mes}-%"
        
        val yearLike = "$ano-%"
        
        return dao.findAllByMonth(monthLike, yearLike)
    }
    
    fun delete(entrada: Entrada) {
        dao.delete(entrada)
    }
    
    fun salvar(entrada: Entrada): Long {
        return dao.persist(entrada)
    }
    
    
}