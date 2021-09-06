package br.com.myself.ui.despesa

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.ArrayAdapter
import com.com.myself.R
import br.com.myself.context.DespesaContext
import br.com.myself.model.Despesa
import br.com.myself.model.exception.ModelException
import br.com.myself.observer.Trigger
import br.com.myself.observer.Events
import br.com.myself.util.CurrencyMask
import br.com.myself.util.Utils
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.dialog_criar_despesa.*
import org.jetbrains.anko.sdk27.coroutines.onFocusChange

class CriarDespesaDialog(context: Context) : Dialog(context) {
    
    init {
        setUpView()
        setUpDimensions()
    }
    
    private fun setUpView() {
        setContentView(R.layout.dialog_criar_despesa)
        
        et_dialog_criar_despesa_valor.apply { addTextChangedListener(
            CurrencyMask(
                this
            )
        ) }
        et_dialog_criar_despesa_valor.onFocusChange { v, hasFocus ->
            if (hasFocus) {
                (v as TextInputEditText).setSelection(v.length())
            }
        }
        
        et_dialog_despesa_dia_vencimento.setAdapter(obterAdapter())
        
        chk_dialog_despesa_sem_vencimento.setOnCheckedChangeListener { _, isChecked ->
            til_dialog_despesa_dia_vencimento.isEnabled = !isChecked
        }
        
        button_dialog_despesa_cadastrar.setOnClickListener {
            val despesa = Despesa()
    
            with(et_dialog_criar_despesa_nome) {
                try {
                    despesa.nome = text.toString()
                    til_dialog_criar_despesa_nome.error = null
                } catch (e: ModelException) {
                    til_dialog_criar_despesa_nome.error = e.message
                }
            }
    
            with(et_dialog_criar_despesa_valor) {
                try {
                    despesa.valor = Utils.unformatCurrency(text.toString()).toDouble()
                    til_dialog_criar_despesa_valor.error = null
                } catch (e: ModelException) {
                    til_dialog_criar_despesa_valor.error = e.message
                }
            }
    
            if (!chk_dialog_despesa_sem_vencimento.isChecked) {
                despesa.diaVencimento = et_dialog_despesa_dia_vencimento.text.toString().toInt()
            }
    
            DespesaContext.getDAO(context).inserir(despesa)
            Trigger.launch(Events.Snack("Despesa adicionada!"), Events.UpdateDespesas())
    
            cancel()
        }
    }
    
    private fun setUpDimensions() {
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window?.attributes)
        
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        
        window?.attributes = lp
    }
    
    private fun obterAdapter(): ArrayAdapter<String> {
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item)
        
        (1..28).forEach { dia -> adapter.add("$dia") }
        
        return adapter
    }
    
}
