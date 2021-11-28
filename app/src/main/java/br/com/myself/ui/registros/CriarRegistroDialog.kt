package br.com.myself.ui.registros

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import com.com.myself.R
import br.com.myself.context.RegistroContext
import br.com.myself.model.Registro
import br.com.myself.observer.Trigger
import br.com.myself.observer.Events
import br.com.myself.util.CurrencyMask
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.getUTCCalendar
import br.com.myself.util.Utils.Companion.formatCurrency
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.dialog_registrar_movimento.*
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import java.math.BigDecimal

class CriarRegistroDialog(context: Context) : Dialog(context) {
    
    private lateinit var onEditedListener: (Registro) -> Unit
    private var edit: Boolean = false
    private var idMovimentoEmEdicao: Int? = null
    
    init {
        setUpView()
        setUpDimensions()
    }
    
    private fun setUpView() {
        setContentView(R.layout.dialog_registrar_movimento)
        
        textinput_dialog_novo_movimento_valor.apply { addTextChangedListener(
            CurrencyMask(
                this
            )
        ) }
        dateinput_dialog_novo_movimento_data.setDate(getUTCCalendar())
        
        button_dialog_novo_movimento_adicionar.setOnClickListener {
            
            val descricao = textinput_dialog_novo_movimento_descricao.text.toString()
            val outros = textinput_dialog_novo_movimento_outros.text.toString()
            val valor = textinput_dialog_novo_movimento_valor.text.toString()
            val data = dateinput_dialog_novo_movimento_data.text.toString()
            
            if (descricao.isBlank()) {
                textinput_dialog_novo_movimento_descricao.error =
                    "Descrição não pode ficar em branco"
            } else if (!isValorValido(valor)) {
                textinput_dialog_novo_movimento_valor.error = "O valor deve ser maior que zero"
            } else if (!Utils.isDataValida(data)) {
                dateinput_dialog_novo_movimento_data.error = "Data inválida"
            } else {
                textinput_dialog_novo_movimento_descricao.error = null
                textinput_dialog_novo_movimento_valor.error = null
                dateinput_dialog_novo_movimento_data.error = null
                
                
                val movimento = Registro()
                movimento.descricao = descricao.trim()
                movimento.valor = Utils.unformatCurrency(valor).toDouble()
                movimento.outros = outros.trim()
                movimento.data = dateinput_dialog_novo_movimento_data.getTime()
                
                
                if (edit) {
                    movimento.id = idMovimentoEmEdicao
                    RegistroContext.getDAO(context).alterar(movimento)
                    Trigger.launch(Events.Toast("Alterado!"))
                    
                    onEditedListener(movimento)
                } else {
                    RegistroContext.getDAO(context).inserir(movimento)
                    Trigger.launch(Events.Toast("Registro adicionado!"))
                }
    
                Trigger.launch(Events.UpdateRegistros())
    
                cancel()
            }
        }
        
        textinput_dialog_novo_movimento_valor.onFocusChange { v, hasFocus ->
            if (hasFocus) (v as TextInputEditText).setSelection(v.length())
        }
    }
    
    private fun setUpDimensions() {
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window?.attributes)
        
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        
        window?.attributes = lp
    }
    
    fun edit(registro: Registro, onEditedListener: (Registro) -> Unit) {
        this.edit = true
        this.idMovimentoEmEdicao = registro.id
        this.onEditedListener = onEditedListener
        
        textinput_dialog_novo_movimento_descricao.setText(registro.descricao)
        textinput_dialog_novo_movimento_descricao.setSelection(registro.descricao?.length ?: 0)
        textinput_dialog_novo_movimento_valor.setText(formatCurrency(registro.valor))
        dateinput_dialog_novo_movimento_data.setDate(registro.data!!)
//        button_dialog_novo_movimento_adicionar.text = "Alterar"
        button_dialog_novo_movimento_adicionar.setImageResource(R.drawable.ic_check)
        tv_dialog_novo_movimento_title.text = "Alterar Registro"
    }
    
    private fun isValorValido(valor: String): Boolean {
        return valor.isNotBlank() && Utils.unformatCurrency(valor).toBigDecimal() > BigDecimal.ZERO
    }
    
}
