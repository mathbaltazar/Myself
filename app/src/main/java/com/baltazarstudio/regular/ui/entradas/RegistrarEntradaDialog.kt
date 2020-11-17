package com.baltazarstudio.regular.ui.entradas

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.EntradaContext
import com.baltazarstudio.regular.model.Entrada
import com.baltazarstudio.regular.observer.Trigger
import com.baltazarstudio.regular.observer.TriggerEvent
import com.baltazarstudio.regular.util.CurrencyMask
import com.baltazarstudio.regular.util.Utils
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.dialog_registrar_entrada.*
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import org.jetbrains.anko.toast
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class RegistrarEntradaDialog(context: Context) : Dialog(context) {
    
    init {
        setContentView(R.layout.dialog_registrar_entrada)
        setupView()
        setUpDimensions()
    }

    private fun setupView() {
        
        dateinput_dialog_nova_entrada_data.setDate(Utils.getUTCCalendar())

        textinput_dialog_nova_entrada_valor.apply { addTextChangedListener(CurrencyMask(this)) }
        
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

                EntradaContext.getDAO(context).inserir(novaEntrada)
                context.toast("Adicionado!")

                Trigger.launch(TriggerEvent.UpdateTelaEntradas())
                dismiss()
            }

        }

        textinput_dialog_nova_entrada_valor.onFocusChange { v, hasFocus ->
            if (hasFocus)
                (v as TextInputEditText).setSelection(v.length())
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
    
}
