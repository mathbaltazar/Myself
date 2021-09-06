package com.baltazarstudio.myself.context

import android.content.Context
import com.baltazarstudio.myself.database.dao.EntradaDAO

class EntradaContext {
    companion object {
        
        private var dao: EntradaDAO? = null
        
        fun getDAO(context: Context): EntradaDAO {
            if (dao == null) dao = EntradaDAO(context)
            return dao!!
        }
    }
}