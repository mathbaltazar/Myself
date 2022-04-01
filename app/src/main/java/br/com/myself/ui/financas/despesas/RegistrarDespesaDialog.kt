package br.com.myself.ui.financas.despesa

import android.app.Dialog
import android.content.Context
import android.view.Window
import br.com.myself.R
import br.com.myself.context.RegistroContext
import br.com.myself.model.Despesa
import br.com.myself.model.Registro
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.util.CurrencyMask
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.setUpDimensions
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.dialog_registrar_despesa.*
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import java.math.BigDecimal

class RegistrarDespesaDialog(context: Context, private val despesa: Despesa) : Dialog(context) {
    
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.white)
        setUpView()
        setUpDimensions(width = (Utils.getScreenSize(context).x * .85).toInt())
    }
    
    private fun setUpView() {
        setContentView(R.layout.dialog_registrar_despesa)
        
        tv_dialog_registrar_despesa_nome.text = despesa.nome
        tv_dialog_registrar_despesa_valor.text = Utils.formatCurrency(despesa.valor)
        
        et_dialog_registrar_despesa_valor.apply {
            setText(Utils.formatCurrency(despesa.valor))
            addTextChangedListener(CurrencyMask(this))
            onFocusChange { v, hasFocus ->
                if (hasFocus) (v as TextInputEditText).setSelection(v.length())
            }
        }
        
        
        button_dialog_registrar_despesa_registrar.setOnClickListener {
            
            val valor = Utils.unformatCurrency(et_dialog_registrar_despesa_valor.text.toString()).toDouble()
            if (valor.toBigDecimal() > BigDecimal.ZERO) {
                til_dialog_registrar_despesa_novo_valor.error = "Valor inválido"
                return@setOnClickListener
            }
            
            // Criação do registro a partir da despesa
            val registro = Registro()
            registro.descricao = despesa.nome
            registro.valor = valor
            registro.data = calendar_picker_dialog_registrar_despesa_data.getTime()
            registro.fk_despesa = despesa.id
            
            RegistroContext.getDAO(context).inserir(registro)
            RegistroContext.atualizarDataView(registro)
            Trigger.launch(Events.Snack(it, "Registrado!"), Events.UpdateRegistros())
            
            cancel()
        }
    }
    
}
