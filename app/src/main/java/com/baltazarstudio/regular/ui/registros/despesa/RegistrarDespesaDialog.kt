package com.baltazarstudio.regular.ui.registros.despesa

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Toast
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.MovimentoContext
import com.baltazarstudio.regular.model.Despesa
import com.baltazarstudio.regular.model.Movimento
import com.baltazarstudio.regular.observer.Trigger
import com.baltazarstudio.regular.observer.TriggerEvent
import com.baltazarstudio.regular.util.CurrencyMask
import com.baltazarstudio.regular.util.DateMask
import com.baltazarstudio.regular.util.Utils
import com.baltazarstudio.regular.util.Utils.Companion.formattedDate
import com.baltazarstudio.regular.util.Utils.Companion.parseDate
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.dialog_registrar_despesa.*
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import org.jetbrains.anko.toast
import java.math.BigDecimal

class RegistrarDespesaDialog(context: Context, private val despesa: Despesa) : Dialog(context) {
    
    init {
        setUpView()
    }
    
    private fun setUpView() {
        setContentView(R.layout.dialog_registrar_despesa)
        
        tv_dialog_registrar_despesa_nome.text = despesa.nome
        tv_dialog_registrar_despesa_valor.text = Utils.formatCurrency(despesa.valor)
        
        et_dialog_registrar_despesa_data.setText(Utils.UTCInstanceCalendar().formattedDate())
        et_dialog_registrar_despesa_data.apply { addTextChangedListener(DateMask(this)) }
        et_dialog_registrar_despesa_data.onFocusChange { v, hasFocus ->
            if (hasFocus) (v as TextInputEditText).setSelection(v.length()) }
        
        et_dialog_registrar_despesa_valor.setText(Utils.formatCurrency(despesa.valor))
        et_dialog_registrar_despesa_valor.apply { addTextChangedListener(CurrencyMask(this)) }
        et_dialog_registrar_despesa_valor.onFocusChange { v, hasFocus ->
            if (hasFocus) (v as TextInputEditText).setSelection(v.length()) }
        
        val chipSingleSelectionBehavior = { tag: String ->
            chip_dialog_registrar_despesa_hoje.isChecked = tag == CHIP_HOJE
            chip_dialog_registrar_despesa_outra_data.isChecked = tag == CHIP_OUTRA_DATA
            
            when (tag) {
                CHIP_OUTRA_DATA -> {
                    til_dialog_registrar_despesa_data.visibility = View.VISIBLE
                }
                CHIP_HOJE -> {
                    til_dialog_registrar_despesa_data.visibility = View.GONE
                    et_dialog_registrar_despesa_data.setText(Utils.UTCInstanceCalendar().formattedDate())
                }
            }
        }
        chip_dialog_registrar_despesa_hoje.tag =
            CHIP_HOJE
        chip_dialog_registrar_despesa_outra_data.tag =
            CHIP_OUTRA_DATA
        
        chip_dialog_registrar_despesa_hoje.setOnClickListener { chipSingleSelectionBehavior(it.tag as String) }
        chip_dialog_registrar_despesa_outra_data.setOnClickListener { chipSingleSelectionBehavior(it.tag as String) }
        
        switch_dialog_registrar_despesa_reajuste.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                til_dialog_registrar_despesa_novo_valor.visibility = View.VISIBLE
                et_dialog_registrar_despesa_valor.setText(Utils.formatCurrency(despesa.valor))
            } else {
                til_dialog_registrar_despesa_novo_valor.visibility = View.GONE
            }
        }
        
        button_dialog_registrar_despesa_voltar.setOnClickListener { cancel() }
        button_dialog_registrar_despesa_registrar.setOnClickListener {
            
            if (chip_dialog_registrar_despesa_outra_data.isChecked) {
                if (!Utils.isDataValida(et_dialog_registrar_despesa_data.text.toString())) {
                    til_dialog_registrar_despesa_data.error = "Data inválida"
                    return@setOnClickListener
                }
            }
            
            val valor = et_dialog_registrar_despesa_valor.text.toString()
            if (switch_dialog_registrar_despesa_reajuste.isChecked) {
                if (!isValorValido(valor)) {
                    til_dialog_registrar_despesa_novo_valor.error = "Valor inválido"
                    return@setOnClickListener
                }
            }
            
            val movimento = Movimento()
            movimento.descricao = despesa.nome
            movimento.valor = Utils.unformatCurrency(valor).toDouble()
            
            val data = et_dialog_registrar_despesa_data.text.toString().parseDate()
            movimento.data = data.time
            
            movimento.referenciaDespesa = despesa.codigo
            movimento.tipoMovimento = Movimento.DESPESA
    
            MovimentoContext.getDAO(context).inserir(movimento)
            Trigger.launch(TriggerEvent.Toast("Registrado!"))
            Trigger.launch(TriggerEvent.UpdateTelaMovimento())
            
            cancel()
        }
    }
    
    private fun isValorValido(valor: String): Boolean {
        return valor.isNotBlank() && Utils.unformatCurrency(valor).toBigDecimal() > BigDecimal.ZERO
    }
    
    companion object {
        private const val CHIP_OUTRA_DATA = "CHIP_OUTRA_DATA"
        private const val CHIP_HOJE = "CHIP_DATA_HOJE"
    }
}
