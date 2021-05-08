package com.baltazarstudio.regular.context

import android.content.Context
import com.baltazarstudio.regular.database.dao.MovimentoDAO
import com.baltazarstudio.regular.model.Movimento

abstract class RegistroContext {
    companion object {
        
        private var mDAO: MovimentoDAO? = null
        
        var textoPesquisa: String? = null
        var registrosParaExcluir: ArrayList<Movimento> = arrayListOf()
    
        fun getDAO(context: Context): MovimentoDAO {
            if (mDAO == null) mDAO = MovimentoDAO(context)
            return mDAO!!
        }
    
        fun excluirMovimentos(context: Context): Int {
            for (movimento in registrosParaExcluir) {
                getDAO(context).excluir(movimento)
            }
            
            return registrosParaExcluir.size
        }
    }
    
}