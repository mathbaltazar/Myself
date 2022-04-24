package br.com.myself.ui.financas.despesas

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.ArrayAdapter
import br.com.myself.R
import br.com.myself.domain.entity.Despesa
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.util.CurrencyMask
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.setUpDimensions
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.dialog_criar_despesa.*
import org.jetbrains.anko.sdk27.coroutines.onFocusChange

class CriarDespesaDialog(context: Context, private val onSave: (Dialog, Despesa) -> Unit) : Dialog(context) {
    
    private var vencimentoSelecionado: Int = 0
    
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.white)
        
        setUpView()
        setUpDimensions(widthPercent = (Utils.getScreenSize(context).x * .85).toInt())
    }
    
    private fun setUpView() {
        setContentView(R.layout.dialog_criar_despesa)
        
        et_dialog_criar_despesa_valor.apply {
            addTextChangedListener(CurrencyMask(this))
            
            onFocusChange { v, hasFocus ->
                if (hasFocus) {
                    (v as TextInputEditText).setSelection(v.length())
                }
            }
        }
    
        with(et_dialog_despesa_dia_vencimento) {
            setAdapter(obterAdapter())
            setOnItemClickListener { _, _, position, _ ->
                vencimentoSelecionado = position
            }
            setText(adapter.getItem(vencimentoSelecionado).toString(), false)
        }
        
        button_dialog_despesa_salvar.setOnClickListener {
            val nome = et_dialog_criar_despesa_nome.text.toString()
            val valor = et_dialog_criar_despesa_valor.text.toString()
            
            if (nome.isBlank()) {
                et_dialog_criar_despesa_nome.requestFocus()
                Trigger.launch(Events.Toast("Nome inválido"))
            } else if (valor.isBlank()) {
                et_dialog_criar_despesa_valor.requestFocus()
                Trigger.launch(Events.Toast("Valor inválido"))
            } else {
                onSave(this, Despesa(
                    nome = nome,
                    valor = Utils.unformatCurrency(valor).toDouble(),
                    diaVencimento = this.vencimentoSelecionado
                ))
            }
            
        }
    }
    
    private fun obterAdapter(): ArrayAdapter<String> {
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item)
        
        adapter.add("Sem vencimento")
        (1..28).forEach { dia -> adapter.add("$dia") }
        
        return adapter
    }
    
}
