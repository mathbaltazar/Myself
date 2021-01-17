package com.baltazarstudio.regular.ui.registros.despesa

import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.DespesaContext
import com.baltazarstudio.regular.context.MovimentoContext
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
    
    private var isEdicao: Boolean = false
    private lateinit var despesaEmEdicao: Despesa
    
    init {
        setUpView()
        setUpDimensions()
    }
    
    private fun setUpView() {
        setContentView(R.layout.dialog_criar_despesa)
        
        et_dialog_despesa_valor.apply { addTextChangedListener(CurrencyMask(this)) }
        et_dialog_despesa_valor.onFocusChange { v, hasFocus ->
            if (hasFocus) {
                (v as TextInputEditText).setSelection(v.length())
            }
        }
        
        et_dialog_despesa_dia_vencimento.setAdapter(obterAdapter())
        
        chk_dialog_despesa_sem_vencimento.setOnCheckedChangeListener { _, isChecked ->
            til_dialog_despesa_dia_vencimento.isEnabled = !isChecked
        }
        
        button_dialog_despesa_cadastrar.setOnClickListener {
            val nome = et_dialog_despesa_nome.text.toString()
            val valor = et_dialog_despesa_valor.text.toString()
            
            if (nome.isBlank()) {
                et_dialog_despesa_nome.error = "Campo obrigatório"
            } else if (!isValorValido(valor)) {
                et_dialog_despesa_valor.error = "Valor inválido"
            } else {
    
                if (isEdicao) {
                    despesaEmEdicao.nome = nome
                    despesaEmEdicao.valor = Utils.unformatCurrency(valor).toDouble()
                    
                    if (chk_dialog_despesa_sem_vencimento.isChecked) {
                        despesaEmEdicao.diaVencimento = 0
                    } else {
                        despesaEmEdicao.diaVencimento = et_dialog_despesa_dia_vencimento.text.toString().toInt()
                    }
                    
                    DespesaContext.getDAO(context).alterar(despesaEmEdicao)
                    
                    Trigger.launch(TriggerEvent.Snack("Despesa alterada"))
                    
                    if (chk_dialog_despesa_alterar_registros.isChecked) {
                        MovimentoContext.getDAO(context).atualizarRegistrosDaDespesa(despesaEmEdicao)
                        Trigger.launch(TriggerEvent.UpdateTelaMovimento())
                    }
                    
                } else {
                    val despesa = Despesa()
                    despesa.nome = nome
                    despesa.valor = Utils.unformatCurrency(valor).toDouble()
    
                    if (!chk_dialog_despesa_sem_vencimento.isChecked) {
                        despesa.diaVencimento = et_dialog_despesa_dia_vencimento.text.toString().toInt()
                    }
                    
                    DespesaContext.getDAO(context).inserir(despesa)
                    Trigger.launch(TriggerEvent.Snack("Despesa adicionada!"))
                }
                
                Trigger.launch(TriggerEvent.UpdateTelaDespesa())
                
                cancel()
            }
        }
    }
    
    private fun setUpDimensions() {
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window?.attributes)
        
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        
        window?.attributes = lp
    }
    
    private fun isValorValido(valor: String): Boolean {
        return valor.isNotBlank() && Utils.unformatCurrency(valor).toBigDecimal() > BigDecimal.ZERO
    }
    
    fun editar(despesa: Despesa) {
        isEdicao = true
        despesaEmEdicao = despesa
        
        et_dialog_despesa_nome.setText(despesaEmEdicao.nome)
        et_dialog_despesa_nome.setSelection(despesaEmEdicao.nome?.length ?: 0)
        et_dialog_despesa_valor.setText(Utils.formatCurrency(despesaEmEdicao.valor))
        
        if (despesaEmEdicao.diaVencimento != 0) {
            et_dialog_despesa_dia_vencimento.setText("${despesaEmEdicao.diaVencimento}")
            et_dialog_despesa_dia_vencimento.setAdapter(obterAdapter())
            
            chk_dialog_despesa_sem_vencimento.isChecked = false
        }
        
        button_dialog_despesa_cadastrar.text = "Alterar"
        tv_dialog_registrar_despesa_title.text = "Editar"
        
        et_dialog_despesa_nome.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (et_dialog_despesa_nome.text.toString().equals(despesaEmEdicao.nome, false)) {
                    chk_dialog_despesa_alterar_registros.visibility = View.GONE
                    chk_dialog_despesa_alterar_registros.isChecked = false
                } else {
                    chk_dialog_despesa_alterar_registros.visibility = View.VISIBLE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        
    }
    
    private fun obterAdapter(): ArrayAdapter<String> {
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item)
        
        (1..28).forEach { dia -> adapter.add("$dia") }
        
        return adapter
    }
    
}
