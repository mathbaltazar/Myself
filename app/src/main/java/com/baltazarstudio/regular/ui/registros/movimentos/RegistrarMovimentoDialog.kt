package com.baltazarstudio.regular.ui.registros.movimentos

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.MovimentoContext
import com.baltazarstudio.regular.model.Movimento
import com.baltazarstudio.regular.observer.Trigger
import com.baltazarstudio.regular.observer.TriggerEvent
import com.baltazarstudio.regular.util.CurrencyMask
import com.baltazarstudio.regular.util.Utils
import com.baltazarstudio.regular.util.Utils.Companion.getUTCCalendar
import com.baltazarstudio.regular.util.Utils.Companion.formatCurrency
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.dialog_registrar_movimento.*
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import java.math.BigDecimal

class RegistrarMovimentoDialog(context: Context) : Dialog(context) {
    
    private lateinit var onEditedListener: (Movimento) -> Unit
    private var edit: Boolean = false
    private var idMovimentoEmEdicao: Int? = null
    
    init {
        setUpView()
        setUpDimensions()
    }
    
    private fun setUpView() {
        setContentView(R.layout.dialog_registrar_movimento)
        
        textinput_dialog_novo_movimento_valor.apply { addTextChangedListener(CurrencyMask(this)) }
        dateinput_dialog_novo_movimento_data.setDate(getUTCCalendar())
        
        button_dialog_novo_movimento_adicionar.setOnClickListener {
            
            val descricao = textinput_dialog_novo_movimento_descricao.text.toString()
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
                
                
                val movimento = Movimento()
                movimento.descricao = descricao
                movimento.valor = Utils.unformatCurrency(valor).toDouble()
                //movimento.data = SimpleDateFormat("dd/MM/yyyy").parse(data).time
                movimento.data = dateinput_dialog_novo_movimento_data.getTime()
                movimento.tipoMovimento = Movimento.GASTO
                
                
                if (edit) {
                    movimento.id = idMovimentoEmEdicao
                    MovimentoContext.getDAO(context).alterar(movimento)
                    Trigger.launch(TriggerEvent.Toast("Alterado!"))
                    
                    onEditedListener(movimento)
                } else {
                    MovimentoContext.getDAO(context).inserir(movimento)
                    Trigger.launch(TriggerEvent.Toast("Movimento adicionado!"))
                }
    
                Trigger.launch(TriggerEvent.UpdateTelaMovimento())
    
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
    
    fun edit(movimento: Movimento, onEditedListener: (Movimento) -> Unit) {
        this.edit = true
        this.idMovimentoEmEdicao = movimento.id
        this.onEditedListener = onEditedListener
        
        textinput_dialog_novo_movimento_descricao.setText(movimento.descricao)
        textinput_dialog_novo_movimento_descricao.setSelection(movimento.descricao?.length ?: 0)
        textinput_dialog_novo_movimento_valor.setText(formatCurrency(movimento.valor))
        dateinput_dialog_novo_movimento_data.setDate(movimento.data!!)
        button_dialog_novo_movimento_adicionar.text = "Alterar"
        tv_dialog_novo_movimento_title.text = "Alterar Movimento"
    }
    
    private fun isValorValido(valor: String): Boolean {
        return valor.isNotBlank() && Utils.unformatCurrency(valor).toBigDecimal() > BigDecimal.ZERO
    }
    
}
