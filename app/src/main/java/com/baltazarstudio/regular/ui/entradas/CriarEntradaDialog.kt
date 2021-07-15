package com.baltazarstudio.regular.ui.entradas

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.EntradaContext
import com.baltazarstudio.regular.model.Entrada
import com.baltazarstudio.regular.observer.Trigger
import com.baltazarstudio.regular.observer.Events
import com.baltazarstudio.regular.util.CurrencyMask
import com.baltazarstudio.regular.util.Utils
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.dialog_criar_entrada.*
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import java.math.BigDecimal

class CriarEntradaDialog(context: Context) : Dialog(context) {
    
    private var isEdit: Boolean = false
    private var idEntradaEmEdicao: Int? = null
    
    init {
        setContentView(R.layout.dialog_criar_entrada)
        setupView()
        setUpDimensions()
    }

    private fun setupView() {
        
        dateinput_dialog_nova_entrada_data.setDate(Utils.getUTCCalendar())

        textinput_dialog_nova_entrada_valor.apply {
            addTextChangedListener(CurrencyMask(this))
            onFocusChange { v, hasFocus ->
                if (hasFocus) (v as TextInputEditText).setSelection(v.length()) }
        }
        
        button_dialog_nova_entrada_adicionar.setOnClickListener {

            val valor = textinput_dialog_nova_entrada_valor.text.toString()
            val descricao = textinput_dialog_nova_entrada_descricao.text.toString()
            val data = dateinput_dialog_nova_entrada_data.text.toString()

            if (descricao.isBlank()) {
                textinput_dialog_nova_entrada_descricao.error =
                    "Descrição não pode ficar em branco"
            } else if (!isValorValido(valor)) {
                textinput_dialog_nova_entrada_valor.error = "O valor deve ser maior que zero"
            } else if (!Utils.isDataValida(data)) {
                dateinput_dialog_nova_entrada_data.error = "Data inválida"
            } else {

                val novaEntrada = Entrada()
                novaEntrada.valor = Utils.unformatCurrency(valor).toDouble()
                novaEntrada.descricao = descricao
                novaEntrada.data = dateinput_dialog_nova_entrada_data.getTime()

                if (isEdit) {
                    novaEntrada.id = idEntradaEmEdicao
                    EntradaContext.getDAO(context).alterar(novaEntrada)
                } else {
                    EntradaContext.getDAO(context).inserir(novaEntrada)
                }

                Trigger.launch(Events.Snack("Adicionado!"), Events.UpdateEntradas())
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
    
    fun edit(entrada: Entrada) {
        this.isEdit = true
        this.idEntradaEmEdicao = entrada.id
    
        textinput_dialog_nova_entrada_descricao.setText(entrada.descricao)
        textinput_dialog_nova_entrada_valor.setText(Utils.formatCurrency(entrada.valor))
        dateinput_dialog_nova_entrada_data.setDate(entrada.data!!)
    
        button_dialog_nova_entrada_adicionar.text = "Salvar"
        tv_dialog_nova_entrada_title.text = "Editar Entrada"
    }
}
