package com.baltazarstudio.regular.context

import android.content.Context
import com.baltazarstudio.regular.database.dao.DespesaDAO

abstract class DespesaContext {
    companion object {
        
        private var mDao: DespesaDAO? = null
        
        fun getDAO(context: Context): DespesaDAO {
            if (mDao == null) mDao = DespesaDAO(context)
            return mDao!!
        }
    }
}