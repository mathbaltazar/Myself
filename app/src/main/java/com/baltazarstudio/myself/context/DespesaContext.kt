package com.baltazarstudio.myself.context

import android.content.Context
import com.baltazarstudio.myself.database.dao.DespesaDAO
import com.baltazarstudio.myself.model.Despesa

abstract class DespesaContext {
    companion object {
        val DETALHES_DESPESA_REQUEST_CODE: Int = 12
        
        private var mDao: DespesaDAO? = null
        
        fun getDAO(context: Context): DespesaDAO {
            if (mDao == null) mDao = DespesaDAO(context)
            return mDao!!
        }
        
        var despesaDetalhada: Despesa? = null
    }
}