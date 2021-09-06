package br.com.myself.context

import android.content.Context
import br.com.myself.database.dao.RegistroDAO
import br.com.myself.model.Registro

abstract class RegistroContext {
    companion object {
        
        private var mDAO: RegistroDAO? = null
        
        var textoPesquisa: String? = null
        var registrosParaExcluir: ArrayList<Registro> = arrayListOf()
    
        fun getDAO(context: Context): RegistroDAO {
            if (mDAO == null) mDAO =
                RegistroDAO(context)
            return mDAO!!
        }
    
        fun excluirMovimentosSelecionados(context: Context): Int {
            for (movimento in registrosParaExcluir) {
                getDAO(context).excluir(movimento)
            }
            
            return registrosParaExcluir.size
        }
    }
    
}