package com.baltazarstudio.regular.context

import android.content.Context
import com.baltazarstudio.regular.database.dao.MovimentoDAO

abstract class MovimentoContext {
    companion object {
        
        private var mDAO: MovimentoDAO? = null
        var useCache: Boolean = false
    
        fun getDAO(context: Context): MovimentoDAO {
            if (mDAO == null) mDAO = MovimentoDAO(context)
            return mDAO!!
        }
    }
    
}