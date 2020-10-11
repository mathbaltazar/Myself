package com.baltazarstudio.regular.ui.registros.entradas

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.EntradaContext
import com.baltazarstudio.regular.model.Entrada
import com.baltazarstudio.regular.util.CurrencyMask
import com.baltazarstudio.regular.util.DateMask
import com.baltazarstudio.regular.util.Utils
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.dialog_registrar_entrada.*
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import org.jetbrains.anko.toast
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class RegistrarEntradaDialog(context: Context) : Dialog(context) {

    var entradaImpl: EntradaInterface? = null

    init {
        setContentView(R.layout.dialog_registrar_entrada)
        setupView()
        setUpDimensions()
    }

    private fun setupView() {

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        textinput_dialog_nova_entrada_data.setText(sdf.format(Date()))
        textinput_dialog_nova_entrada_data.apply { addTextChangedListener(DateMask(this)) }

        textinput_dialog_nova_entrada_valor.apply { addTextChangedListener(CurrencyMask(this)) }
        
        button_dialog_nova_entrada_adicionar.setOnClickListener {

            val valor = textinput_dialog_nova_entrada_valor.text.toString()
            val descricao = textinput_dialog_nova_entrada_descricao.text.toString()
            val data = textinput_dialog_nova_entrada_data.text.toString()

            if (descricao.isBlank()) {
                textinput_dialog_nova_entrada_descricao.error =
                    "Descrição não pode ficar em branco"
            } else if (!isValorValido(valor)) {
                textinput_dialog_nova_entrada_valor.error = "O valor deve ser maior que zero"
            } else if (!Utils.isDataValida(data)) {
                textinput_dialog_nova_entrada_data.error = "Data inválida"
            } else {

                val novaEntrada = Entrada()
                novaEntrada.valor = Utils.unformatCurrency(valor).toDouble()
                novaEntrada.descricao = descricao
                novaEntrada.data = sdf.parse(data)?.time

                EntradaContext.getDAO(context).inserir(novaEntrada)
                context.toast("Adicionado!")

                entradaImpl?.onEntradaAdicionada(novaEntrada)
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

        ///val height = Utils.getScreenSize(context).y * 0.5 // %
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        window?.attributes = lp
    }

    private fun isValorValido(valor: String): Boolean {
        return valor.isNotBlank() && Utils.unformatCurrency(valor).toBigDecimal() > BigDecimal.ZERO
    }

    interface EntradaInterface {
        fun onEntradaAdicionada(entrada: Entrada)
    }
    
    fun setEntradaInterface(entradaImpl: EntradaInterface) {
        this.entradaImpl = entradaImpl
    }
}
