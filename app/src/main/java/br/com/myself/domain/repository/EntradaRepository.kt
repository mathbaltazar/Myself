package br.com.myself.domain.repository

import android.app.Application
import br.com.myself.domain.database.MyDatabase
import br.com.myself.domain.entity.Entrada

class EntradaRepository(application: Application) {
    
    private val dao = MyDatabase.getInstance(application).getEntradaDAO()
    
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