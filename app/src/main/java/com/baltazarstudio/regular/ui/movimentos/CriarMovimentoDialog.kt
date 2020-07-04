package com.baltazarstudio.regular.ui.movimentos

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.database.dao.MovimentoDAO
import com.baltazarstudio.regular.model.Movimento
import com.baltazarstudio.regular.util.CurrencyMask
import com.baltazarstudio.regular.util.Utils
import com.baltazarstudio.regular.util.Utils.Companion.formattedDate
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.dialog_criar_movimento.*
import java.math.BigDecimal
import java.util.*

class CriarMovimentoDialog(
    context: Context,
    private var fragmentManager: FragmentManager
) : Dialog(context) {

    private var edit: Boolean = false
    private var id: Int? = null

    init {
        setContentView(R.layout.dialog_criar_movimento)
        setUpView()
        setUpDimensions()
    }

    private fun setUpView() {
        textinput_dialog_novo_movimento_valor.apply { addTextChangedListener(CurrencyMask(this)) }

        textinput_dialog_novo_movimento_data.setText(Utils.UTCInstanceCalendar().formattedDate())

        val today = Utils.UTCInstanceCalendar().timeInMillis
        val picker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setEnd(today)
                    .setOpenAt(today)
                    .build()
            )
            .build()
        picker.addOnPositiveButtonClickListener { timeInMillis ->
            val calendar = Utils.UTCInstanceCalendar()
            calendar.timeInMillis = timeInMillis
            calendar.roll(Calendar.DAY_OF_MONTH, 1)
            textinput_dialog_novo_movimento_data.setText(calendar.formattedDate())
        }

        til_dialog_novo_movimento_data.setEndIconOnClickListener {
            picker.show(fragmentManager, picker.toString())
        }

        button_dialog_novo_movimento_adicionar.setOnClickListener {

            val descricao = textinput_dialog_novo_movimento_descricao.text.toString()
            val valor = textinput_dialog_novo_movimento_valor.text.toString()
            var data = textinput_dialog_novo_movimento_data.text.toString()

            if (descricao.isBlank()) {
                textinput_dialog_novo_movimento_descricao.error =
                    "Descrição não pode ficar em branco"
            } else if (!isValorValido(valor)) {
                textinput_dialog_novo_movimento_valor.error = "O valor deve ser maior que zero"
            } else {
                textinput_dialog_novo_movimento_descricao.error = null
                textinput_dialog_novo_movimento_valor.error = null


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

    /*fun edit(movimento: Movimento): Dialog {
        edit = true
        id = movimento.id

        textinput_dialog_novo_movimento_descricao.setText(movimento.descricao)
        textinput_dialog_novo_movimento_descricao.setText(Utils.formatCurrency(movimento.valor))
        button_dialog_novo_movimento_adicionar.text = "Alterar"

        return this
    }*/

    private fun isValorValido(valor: String): Boolean {
        return valor.isNotBlank() && Utils.unformatCurrency(valor).toBigDecimal() > BigDecimal.ZERO
    }

}
