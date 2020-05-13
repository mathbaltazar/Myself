package com.baltazarstudio.regular.ui.pendencia

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Toast
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.database.dao.MovimentoDAO
import com.baltazarstudio.regular.model.Movimento
import com.baltazarstudio.regular.util.CurrencyMask
import com.baltazarstudio.regular.util.DateMask
import com.baltazarstudio.regular.util.Utils
import com.baltazarstudio.regular.util.Utils.Companion.formattedDate
import kotlinx.android.synthetic.main.dialog_criar_movimento.*
import java.math.BigDecimal
import java.util.*

class CriarMovimentoDialog(context: Context) : Dialog(context) {
    private var edit: Boolean = false
    private var id: Int? = null

    init {
        onCreate()
        setUpDimensions()
    }

    private fun onCreate() {
        setContentView(R.layout.dialog_criar_movimento)

        textinput_dialog_novo_movimento_valor.apply { addTextChangedListener(CurrencyMask(this)) }

        textinput_dialog_novo_movimento_data.setText(Calendar.getInstance().formattedDate())
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
                textinput_dialog_novo_movimento_data.error = "A data é inválida"
            } else {
                textinput_dialog_novo_movimento_descricao.error = null
                textinput_dialog_novo_movimento_valor.error = null
                textinput_dialog_novo_movimento_data.error = null


                val movimento = Movimento()
                movimento.descricao = descricao
                movimento.valor = Utils.unformatCurrency(valor).toDouble()

                if (edit) {
                    MovimentoDAO(context).alterar(movimento)
                    Toast.makeText(context, "Alterado!", Toast.LENGTH_LONG).show()
                } else {
                    data = data.replace("/", "")
                    movimento.dia = data.substring(0, 2).toInt()
                    movimento.mes = data.substring(2, 4).toInt() - 1
                    movimento.ano = data.substring(4).toInt()
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
        id = movimento.id

        textinput_dialog_novo_movimento_descricao.setText(movimento.descricao)
        textinput_dialog_novo_movimento_descricao.setText(Utils.formatCurrency(movimento.valor))
        button_dialog_novo_movimento_adicionar.text = "Alterar"

        return this
    }

    private fun isValorValido(valor: String): Boolean {
        return valor.isNotBlank() && Utils.unformatCurrency(valor).toBigDecimal() > BigDecimal.ZERO
    }

    private fun isDataValida(data: String): Boolean {
        val str = data.replace("/", "")
        if (str.length < 8)
            return false

        val dia = str.substring(0, 2)
        val mes = str.substring(2, 4)
        val ano = str.substring(4)

        val calendar = Calendar.getInstance()
        try {
            if (ano.toInt() == 0 || ano.toInt() > calendar.get(Calendar.YEAR)) {
                return false
            } else if (mes.toInt() == 0 || mes.toInt() > 12) {
                return false
            } else if (mes.toInt() == 2) {
                if (ano.toInt() % 4 == 0 && dia.toInt() > 29) {
                    return false
                } else if (ano.toInt() % 4 != 0 && dia.toInt() > 28) {
                    return false
                }
            } else if (dia.toInt() == 0 || dia.toInt() > 31) {
                return false
            }

            calendar.set(ano.toInt(), mes.toInt() - 1, dia.toInt())
            val timeMillis = calendar.timeInMillis

            if (timeMillis > Calendar.getInstance().timeInMillis) {
                return false
            }
        } catch (error: ClassCastException) {
            error.printStackTrace()
            return false
        }

        return true
    }

}
