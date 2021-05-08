package com.baltazarstudio.regular.ui.despesa

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.DespesaContext
import com.baltazarstudio.regular.ui.adapter.SelecionarDespesaAdapter
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.dialog_selecionar_despesa.*

class SelecionarDespesaDialog(context: Context, onItemSelected: (Int) -> Unit) : Dialog(context) {
    
    
    
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_selecionar_despesa)
    
        val despesas = DespesaContext.getDAO(context).getTodasDespesas()
        
        if (despesas.isEmpty()) {
            tv_dialog_selecionar_despesa_sem_despesa.visibility = View.VISIBLE
        } else {
            rv_selecionar_despesa.adapter = SelecionarDespesaAdapter(context, despesas) { codigo ->
                onItemSelected(codigo)
                cancel()
            }
            rv_selecionar_despesa.layoutManager = LinearLayoutManager(context)
        }
        
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
