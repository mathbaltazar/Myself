package com.baltazarstudio.regular.context

import android.content.Context
import com.baltazarstudio.regular.database.dao.EntradaDAO

class EntradaContext {
    companion object {
        
        private var dao: EntradaDAO? = null
        
        fun getDAO(context: Context): EntradaDAO {
            if (dao == null) dao = EntradaDAO(context)
            return dao!!
        }
    }
}