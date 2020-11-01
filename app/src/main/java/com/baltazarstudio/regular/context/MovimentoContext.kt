package com.baltazarstudio.regular.context

import android.content.Context
import com.baltazarstudio.regular.database.dao.MovimentoDAO
import com.baltazarstudio.regular.model.Movimento

abstract class MovimentoContext {
    companion object {
        
        private var mDAO: MovimentoDAO? = null
        var useCache: Boolean = false
        var movimentosParaExcluir: ArrayList<Movimento> = arrayListOf()
    
        fun getDAO(context: Context): MovimentoDAO {
            if (mDAO == null) mDAO = MovimentoDAO(context)
            return mDAO!!
        }
    
        fun excluirMovimentos(context: Context) {
            for (movimento in movimentosParaExcluir) {
                getDAO(context).excluir(movimento)
            }
        }
    }
    
}