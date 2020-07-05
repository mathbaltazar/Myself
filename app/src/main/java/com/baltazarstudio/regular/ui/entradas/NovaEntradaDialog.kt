package com.baltazarstudio.regular.ui.entradas

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.ArrayAdapter
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.database.dao.EntradaDAO
import com.baltazarstudio.regular.model.Entrada
import com.baltazarstudio.regular.util.CurrencyMask
import com.baltazarstudio.regular.util.DateMask
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.dialog_nova_entrada.*
import org.jetbrains.anko.toast
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class NovaEntradaDialog(
    context: Context,
    private val dao: EntradaDAO,
    private val donos: List<String>
) : Dialog(context) {

    var isAdicionado: Boolean = false

    init {
        setContentView(R.layout.dialog_nova_entrada)
        setupView()
        setUpDimensions()
    }

    private fun setupView() {
        val adapter = ArrayAdapter(context, R.layout.custom_spinner_item, donos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_dialog_nova_entrada_dono.adapter = adapter



        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        textinput_dialog_nova_entrada_data.setText(sdf.format(Date()))
        textinput_dialog_nova_entrada_data.apply { addTextChangedListener(DateMask(this)) }

        textinput_dialog_nova_entrada_valor.apply { addTextChangedListener(CurrencyMask(this)) }







        button_dialog_nova_entrada_adicionar.setOnClickListener {

            val valor = textinput_dialog_nova_entrada_valor.text.toString()
            val descricao = textinput_dialog_nova_entrada_descricao.text.toString()
            val data = textinput_dialog_nova_entrada_data.text.toString()
            val dono = spinner_dialog_nova_entrada_dono.selectedItem?.toString()

            if (descricao.isBlank()) {
                textinput_dialog_nova_entrada_descricao.error =
                    "Descrição não pode ficar em branco"
            } else if (!isValorValido(valor)) {
                textinput_dialog_nova_entrada_valor.error = "O valor deve ser maior que zero"
            } else if (!Utils.isDataValida(data)) {
                textinput_dialog_nova_entrada_data.error = "Data inválida"
            } else if (dono.isNullOrBlank()) {
                context.toast("Não há dono selecionado!")
            } else {

                val novaEntrada = Entrada()
                novaEntrada.valor = Utils.unformatCurrency(valor).toDouble()
                novaEntrada.descricao = descricao
                novaEntrada.data = sdf.parse(data)?.time
                novaEntrada.dono = dono

                dao.inserir(novaEntrada)
                context.toast("Adicionado!")

                isAdicionado = true
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

    private fun isValorValido(valor: String): Boolean {
        return valor.isNotBlank() && Utils.unformatCurrency(valor).toBigDecimal() > BigDecimal.ZERO
    }


}
