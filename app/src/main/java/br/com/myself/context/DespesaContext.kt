package br.com.myself.context

import android.content.Context
import br.com.myself.model.dao.DespesaDAO
import br.com.myself.model.entity.Despesa
import br.com.myself.ui.financas.despesas.DespesasFragment

abstract class DespesaContext {
    companion object {
        private val despesasDataView = DespesasFragment.DespesaDataViewObject()
        private var loaded:Boolean = false
        
        fun getDataView(context: Context): DespesasFragment.DespesaDataViewObject {
            if (!loaded) {
                obterDespesas(context)
                loaded = true
            }
            return despesasDataView
        }
    
        private var mDao: DespesaDAO? = null
        
        fun getDAO(context: Context): DespesaDAO {
            if (mDao == null) mDao =
                DespesaDAO(context)
            return mDao!!
        }
    
        fun obterDespesas(context: Context) {
            despesasDataView.despesas.clear()
            despesasDataView.despesas.addAll(getDAO(context).getTodasDespesas())
        }
    
        fun atualizarDespesa(despesa: Despesa) {
            despesasDataView.despesas.add(despesa)
            despesasDataView.despesas.sortByDescending { it.id }
        }
    
        fun removerDespesa(despesa: Despesa) {
            despesasDataView.despesas.remove(despesa)
        }
    
    }
}