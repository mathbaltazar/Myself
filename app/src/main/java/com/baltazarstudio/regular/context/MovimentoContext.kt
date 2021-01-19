package com.baltazarstudio.regular.context

import android.content.Context
import com.baltazarstudio.regular.database.dao.MovimentoDAO
import com.baltazarstudio.regular.model.Movimento

abstract class MovimentoContext {
    companion object {
        
        private var mDAO: MovimentoDAO? = null
        
        var textoPesquisa: String? = null
        var movimentosParaExcluir: ArrayList<Movimento> = arrayListOf()
    
        fun getDAO(context: Context): MovimentoDAO {
            if (mDAO == null) mDAO = MovimentoDAO(context)
            return mDAO!!
        }
    
        fun excluirMovimentos(context: Context): Int {
            for (movimento in movimentosParaExcluir) {
                getDAO(context).excluir(movimento)
            }
            
            return movimentosParaExcluir.size
        }
    }
    
}