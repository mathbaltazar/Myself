package com.baltazarstudio.regular.ui.movimentos

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Toast
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.controller.GastosController
import com.baltazarstudio.regular.model.Gasto
import com.baltazarstudio.regular.util.CurrencyMask
import com.baltazarstudio.regular.util.DateMask
import com.baltazarstudio.regular.util.Utils
import com.baltazarstudio.regular.util.Utils.Companion.UTCInstanceCalendar
import com.baltazarstudio.regular.util.Utils.Companion.formatCurrency
import com.baltazarstudio.regular.util.Utils.Companion.formattedDate
import com.baltazarstudio.regular.util.Utils.Companion.isDataValida
import kotlinx.android.synthetic.main.dialog_criar_movimento.*
import java.math.BigDecimal
import java.text.SimpleDateFormat

class GastoDialog(
    context: Context, private val controller: GastosController
) : Dialog(context) {
    
    private var edit: Boolean = false
    private var id: Int? = null
    
    init {
        setUpView()
        setUpDimensions()
    }
    
    private fun setUpView() {
        setContentView(R.layout.dialog_criar_movimento)
        
        textinput_dialog_novo_movimento_valor.apply { addTextChangedListener(CurrencyMask(this)) }
        
        textinput_dialog_novo_movimento_data.setText(UTCInstanceCalendar().formattedDate())
        textinput_dialog_novo_movimento_data.apply { addTextChangedListener(DateMask(this)) }
        
        button_dialog_novo_movimento_adicionar.setOnClickListener {
            
            val descricao = textinput_dialog_novo_movimento_descricao.text.toString()
            val valor = textinput_dialog_novo_movimento_valor.text.toString()
            var data = textinput_dialog_novo_movimento_data.text.toString()
            
            if (descricao.isBlank()) {
                textinput_dialog_novo_movimento_descricao.error =
                    "Descrição não pode ficar em branco"
            } else if (!isValorValido(valor)) {
                textinput_dialog_novo_movimento_valor.error = "O valor deve ser maior que zero"
            } else if (!isDataValida(data)) {
                textinput_dialog_novo_movimento_data.error = "Data inválida"
            } else {
                textinput_dialog_novo_movimento_descricao.error = null
                textinput_dialog_novo_movimento_valor.error = null
                textinput_dialog_novo_movimento_data.error = null
                
                
                val gasto = Gasto()
                gasto.descricao = descricao
                gasto.valor = Utils.unformatCurrency(valor).toDouble()
                gasto.data = SimpleDateFormat("dd/MM/yyyy").parse(data).time
                
                
                data = data.replace("/", "")
                gasto.mes = data.substring(2, 4).toInt()
                gasto.ano = data.substring(4).toInt()
                
                
                if (edit) {
                    gasto.id = id
                    controller.alterar(gasto)
                    Toast.makeText(context, "Alterado!", Toast.LENGTH_LONG).show()
                } else {
                    controller.inserir(gasto)
                    Toast.makeText(context, "Movimento adicionado!", Toast.LENGTH_LONG).show()
                }
                
                controller.carregarGastos()
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
    
    fun edit(gasto: Gasto): Dialog {
        edit = true
        id = gasto.id
        
        textinput_dialog_novo_movimento_descricao.setText(gasto.descricao)
        textinput_dialog_novo_movimento_valor.setText(formatCurrency(gasto.valor))
        textinput_dialog_novo_movimento_data.setText(gasto.data.formattedDate())
        button_dialog_novo_movimento_adicionar.text = "Alterar"
        tv_dialog_novo_movimento_title.text = "Alterar Movimento"
        
        return this
    }
    
    private fun isValorValido(valor: String): Boolean {
        return valor.isNotBlank() && Utils.unformatCurrency(valor).toBigDecimal() > BigDecimal.ZERO
    }
    
}
