package br.com.myself.context

import android.content.Context
import br.com.myself.domain.dao.BackupDAO

class ConfigContext {
    companion object {
        
        private var mDAO: BackupDAO? = null
        
        fun getDAO(context: Context): BackupDAO {
            if (mDAO == null) mDAO =
                BackupDAO(context)
            return mDAO!!
        }
    }
}