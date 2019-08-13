package com.baltazarstudio.regular.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.adapter.ItemCarteiraRegistroAdapter
import com.baltazarstudio.regular.database.ItemCarteiraAbertaDAO
import com.baltazarstudio.regular.database.RegistroItemDAO
import com.baltazarstudio.regular.model.ItemCarteiraAberta
import com.baltazarstudio.regular.model.RegistroItem
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.activity_detalhes_item_carteira.*
import kotlinx.android.synthetic.main.layout_detalhes_item_carteira_input_registro.view.*
import java.math.BigDecimal

class DetalhesItemCarteiraActivity : AppCompatActivity() {

    lateinit var item: ItemCarteiraAberta

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_item_carteira)
        supportActionBar?.title = getString(R.string.activity_title_detalhes_item_carteira)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        init()
    }

    private fun init() {
        item = ItemCarteiraAbertaDAO(this).get(intent.getIntExtra("id", 0))

        tv_item_carteira_valor.text = Utils.formatCurrency(item.valor)
        tv_item_carteira_data.text = item.data
        tv_item_carteira_descricao.text = item.descricao
        tv_item_carteira_valor_pago.text = calcularValorPago(item.registros)

        button_detalhes_item_carteira_novo_ajuste.setOnClickListener {
            createDialogNovoAjuste()
        }

        button_detalhes_item_carteira_marcar_como_pago.setOnClickListener {
            marcarComoPago()
        }

        if (item.registros.size > 0) {
            tv_sem_registros_item.visibility = View.GONE
            listview_item_carteira_registro.adapter = ItemCarteiraRegistroAdapter(this, item.registros)
        } else {
            tv_sem_registros_item.visibility = View.VISIBLE
        }
    }

    @SuppressLint("InflateParams")
    private fun createDialogNovoAjuste() {
        val dialogView = layoutInflater.inflate(R.layout.layout_detalhes_item_carteira_input_registro, null)
        val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

        dialogView.button_inserir_item_registro.setOnClickListener {
            if (dialogView.textinput_item_registro_valor.text.toString() == "") {
                dialogView.textinput_item_registro_error.visibility = View.VISIBLE
            } else {
                val novoRegistro = RegistroItem()
                novoRegistro.descricao = dialogView.textinput_item_registro_descricao.text.toString()
                novoRegistro.valor = BigDecimal(dialogView.textinput_item_registro_valor.text.toString())
                novoRegistro.itemCarteiraAberta = item

                RegistroItemDAO(this).inserir(novoRegistro)

                dialog.dismiss()
                Toast.makeText(this, R.string.toast_detalhe_item_carteira_registro_adicionado, Toast.LENGTH_LONG).show()
            }
        }

        dialog.show()
    }

    private fun marcarComoPago() {

    }

    private fun calcularValorPago(registros: ArrayList<RegistroItem>): String {
        var soma = BigDecimal.ZERO
        for (registro in registros) {
            soma = soma.add(registro.valor)
        }
        return Utils.formatCurrency(soma)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
