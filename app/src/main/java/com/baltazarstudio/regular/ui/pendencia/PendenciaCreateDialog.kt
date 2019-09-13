package com.baltazarstudio.regular.ui.pendencia

import android.app.Dialog
import android.content.Context
import android.widget.Toast
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.database.dao.PendenciaDAO
import com.baltazarstudio.regular.model.Pendencia
import com.baltazarstudio.regular.util.CurrencyMask
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.dialog_add_pendencia.*
import java.math.BigDecimal

class PendenciaCreateDialog(context: Context) : Dialog(context) {

    private var item = Pendencia()
    private var edit: Boolean = false

    init {
        onCreate()
    }

    private fun onCreate() {
        setContentView(R.layout.dialog_add_pendencia)

        textinput_dialog_add_pendencia_valor
                .addTextChangedListener(CurrencyMask(textinput_dialog_add_pendencia_valor))

        button_dialog_add_pendencia_adicionar.setOnClickListener {

            val descricao = textinput_dialog_add_pendencia_descricao.text.toString()
            val valor = textinput_dialog_add_pendencia_valor.text.toString()

            if (descricao == "") {
                textinput_dialog_add_pendencia_descricao.error = "Descrição não pode ficar em branco"
            } else if (!isValorValido(valor)) {
                textinput_dialog_add_pendencia_valor.error = "O valor deve ser maior que zero"
            } else {
                textinput_dialog_add_pendencia_descricao.error = null
                textinput_dialog_add_pendencia_valor.error = null

                item.descricao = descricao
                item.valor = Utils.unformatCurrency(valor).toBigDecimal()

                if (edit) {
                    PendenciaDAO(context).alterar(item)
                    Toast.makeText(context, R.string.toast_pendencia_alterada, Toast.LENGTH_LONG).show()
                } else {
                    item.data = Utils.currentDateFormatted()
                    PendenciaDAO(context).inserir(item)
                    Toast.makeText(context, R.string.toast_pendencia_adicionada, Toast.LENGTH_LONG).show()
                }

                dismiss()
            }
        }
    }

    fun edit(pendencia: Pendencia): Dialog {
        edit = true
        item = pendencia
        bindEdit()
        return this
    }

    private fun bindEdit() {
        textinput_dialog_add_pendencia_descricao.setText(item.descricao)
        textinput_dialog_add_pendencia_descricao.setText(Utils.formatCurrency(item.valor))
        button_dialog_add_pendencia_adicionar.text = "Alterar"
    }

    private fun isValorValido(valor: String): Boolean {
        return valor != "" && Utils.unformatCurrency(valor).toBigDecimal() > BigDecimal.ZERO
    }

}
