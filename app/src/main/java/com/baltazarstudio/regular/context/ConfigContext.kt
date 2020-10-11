package com.baltazarstudio.regular.context

import android.content.Context
import com.baltazarstudio.regular.database.dao.ConfiguracaoDAO

class ConfigContext {
    companion object {
        
        private var mDAO: ConfiguracaoDAO? = null
        
        fun getDAO(context: Context): ConfiguracaoDAO {
            if (mDAO == null) mDAO = ConfiguracaoDAO(context)
            return mDAO!!
        }
    }
}