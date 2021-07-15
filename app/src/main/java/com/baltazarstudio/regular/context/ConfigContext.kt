package com.baltazarstudio.regular.context

import android.content.Context
import com.baltazarstudio.regular.database.dao.BackupDAO

class ConfigContext {
    companion object {
        
        private var mDAO: BackupDAO? = null
        
        fun getDAO(context: Context): BackupDAO {
            if (mDAO == null) mDAO = BackupDAO(context)
            return mDAO!!
        }
    }
}