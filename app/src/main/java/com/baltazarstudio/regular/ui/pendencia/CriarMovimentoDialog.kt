package com.baltazarstudio.regular.ui.pendencia

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Toast
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.database.dao.MovimentoDAO
import com.baltazarstudio.regular.model.Movimento
import com.baltazarstudio.regular.util.CurrencyMask
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.dialog_criar_movimento.*
import java.math.BigDecimal
import java.util.*

class CriarMovimentoDialog(context: Context) : Dialog(context) {

    private var movimento = Movimento()
    private var edit: Boolean = false

    init {
        onCreate()
        setUpDimensions()
    }

    private fun onCreate() {
        setContentView(R.layout.dialog_criar_movimento)

        textinput_dialog_novo_movimento_valor.apply { addTextChangedListener(CurrencyMask(this)) }

        button_dialog_novo_movimento_adicionar.setOnClickListener {

            val descricao = textinput_dialog_novo_movimento_descricao.text.toString()
            val valor = textinput_dialog_novo_movimento_valor.text.toString()

            if (descricao.isBlank()) {
                textinput_dialog_novo_movimento_descricao.error = "Descrição não pode ficar em branco"
            } else if (!isValorValido(valor)) {
                textinput_dialog_novo_movimento_valor.error = "O valor deve ser maior que zero"
            } else {
                textinput_dialog_novo_movimento_descricao.error = null
                textinput_dialog_novo_movimento_valor.error = null

                movimento.descricao = descricao
                movimento.valor = Utils.unformatCurrency(valor).toBigDecimal()

                if (edit) {
                    MovimentoDAO(context).alterar(movimento)
                    Toast.makeText(context, "Alterado!", Toast.LENGTH_LONG).show()
                } else {
                    movimento.data = Date().time
                    MovimentoDAO(context).inserir(movimento)
                    Toast.makeText(context, "Movimento adicionado!", Toast.LENGTH_LONG).show()
                }

                dismiss()
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

    fun edit(movimento: Movimento): Dialog {
        edit = true
        this.movimento = movimento

        textinput_dialog_novo_movimento_descricao.setText(this.movimento.descricao)
        textinput_dialog_novo_movimento_descricao.setText(Utils.formatCurrency(this.movimento.valor))
        button_dialog_novo_movimento_adicionar.text = "Alterar"

        return this
    }

    private fun isValorValido(valor: String): Boolean {
        return valor.isNotBlank() && Utils.unformatCurrency(valor).toBigDecimal() > BigDecimal.ZERO
    }

}
