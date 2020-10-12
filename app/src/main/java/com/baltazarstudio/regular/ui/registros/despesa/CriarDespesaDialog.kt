package com.baltazarstudio.regular.ui.registros.despesa

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Toast
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.DespesaContext
import com.baltazarstudio.regular.model.Despesa
import com.baltazarstudio.regular.observer.Trigger
import com.baltazarstudio.regular.observer.TriggerEvent
import com.baltazarstudio.regular.util.CurrencyMask
import com.baltazarstudio.regular.util.Utils
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.dialog_criar_despesa.*
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import java.math.BigDecimal

class CriarDespesaDialog(context: Context) : Dialog(context) {
    
    init {
        setUpView()
        setUpDimensions()
    }
    
    private fun setUpView() {
        setContentView(R.layout.dialog_criar_despesa)
        
        et_dialog_despesa_valor.apply { addTextChangedListener(CurrencyMask(this)) }
        et_dialog_despesa_valor.onFocusChange { v, hasFocus ->
            if (hasFocus) (v as TextInputEditText).setSelection(v.length()) }
        
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
                
                DespesaContext.getDAO(context).inserir(despesa)
                Toast.makeText(context, "Despesa adicionada!", Toast.LENGTH_SHORT).show()
                
                Trigger.launch(TriggerEvent.UpdateTelaDespesa())
                
                cancel()
            }
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
