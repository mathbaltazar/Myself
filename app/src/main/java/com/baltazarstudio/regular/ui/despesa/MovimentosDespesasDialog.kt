package com.baltazarstudio.regular.ui.despesa

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.model.Movimento
import com.baltazarstudio.regular.ui.adapter.RegistrosDaDespesaAdapter
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.dialog_movimentos_despesas.*

class MovimentosDespesasDialog(context: Context, movimentos: ArrayList<Movimento>) : Dialog(context){
    
    
    init {
        setContentView(R.layout.dialog_movimentos_despesas)
    
        rv_dialog_registros_despesa.adapter = RegistrosDaDespesaAdapter(context, movimentos)
        rv_dialog_registros_despesa.layoutManager = LinearLayoutManager(context)
        rv_dialog_registros_despesa.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        
        setUpDimensions()
    }
    
    private fun setUpDimensions() {
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window?.attributes)
        
        lp.width = (Utils.getScreenSize(context).x * .9).toInt()
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        
        window?.attributes = lp
    }
}
