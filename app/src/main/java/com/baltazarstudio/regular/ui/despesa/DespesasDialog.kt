package com.baltazarstudio.regular.ui.despesa

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Toast
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.controller.DespesasController
import com.baltazarstudio.regular.model.Despesa
import com.baltazarstudio.regular.util.CurrencyMask
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.dialog_despesa.*
import java.math.BigDecimal

class DespesasDialog(context: Context, private val controller: DespesasController) : Dialog(context) {
    
    init {
        setUpView()
        setUpDimensions()
    }
    
    private fun setUpView() {
        setContentView(R.layout.dialog_despesa)
        
        et_dialog_despesa_valor.apply { addTextChangedListener(CurrencyMask(this)) }
        
        button_dialog_despesa_cadastrar.setOnClickListener {
            val nome = et_dialog_despesa_nome.text.toString()
            val valor = et_dialog_despesa_valor.text.toString()
            
            if (nome.isBlank()) {
                et_dialog_despesa_nome.error = "Campo obrigatório"
            } else if (!isValorValido(valor)) {
                et_dialog_despesa_valor.error = "Valor inválido"
            } else {
                
                val despesa = Despesa()
                despesa.nome = nome
                despesa.valor = Utils.unformatCurrency(valor).toDouble()
                
                controller.inserirDespesa(despesa)
                Toast.makeText(context, "Despesa adicionada!", Toast.LENGTH_SHORT).show()
                cancel()
            }
        }
        
        setOnDismissListener {
            controller.carregarDespesas()
        }
    }
    
    private fun setUpDimensions() {
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window?.attributes)
        
        ///val height = Utils.getScreenSize(context).y * 0.5 // %
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        
        window?.attributes = lp
    }
    
    private fun isValorValido(valor: String): Boolean {
        return valor.isNotBlank() && Utils.unformatCurrency(valor).toBigDecimal() > BigDecimal.ZERO
    }
    
}
