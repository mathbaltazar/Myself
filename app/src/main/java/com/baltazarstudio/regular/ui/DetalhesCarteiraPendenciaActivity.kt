package com.baltazarstudio.regular.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.adapter.RegistroItemCarteiraAdapter
import com.baltazarstudio.regular.database.dao.CarteiraPendenciaDAO
import com.baltazarstudio.regular.database.dao.RegistroItemDAO
import com.baltazarstudio.regular.model.CarteiraPendencia
import com.baltazarstudio.regular.model.RegistroItem
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.activity_detalhes_item_carteira.*
import kotlinx.android.synthetic.main.dialog_add_element.view.*
import java.math.BigDecimal

class DetalhesCarteiraPendenciaActivity : AppCompatActivity() {

    lateinit var item: CarteiraPendencia
    private val registroItemDAO = RegistroItemDAO(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_item_carteira)
        supportActionBar?.title = getString(R.string.activity_title_detalhes_pendencia)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        button_detalhes_item_carteira_novo_ajuste.setOnClickListener {
            createDialogNovoAjuste()
        }

        button_detalhes_item_carteira_marcar_como_pago.setOnClickListener {
            marcarComoPago()
        }

        listview_registro_item_carteira.setOnItemLongClickListener { adapterView, _, position, _ ->
            createDialogExcluir(adapterView.adapter.getItem(position) as RegistroItem)
        }

        refreshDados()
    }

    private fun refreshDados() {
        item = CarteiraPendenciaDAO(this).get(intent.getIntExtra("id", 0))

        tv_item_pendencia_valor.text = Utils.formatCurrency(item.valor)
        tv_item_carteira_data.text = item.data
        tv_item_pendencia_descricao.text = item.descricao

        val valorPago = calcularValorPago(item.registros)
        tv_item_carteira_valor_pago.text = Utils.formatCurrency(valor = valorPago)


        if (valorPago > item.valor)
            tv_aviso_valor_ultrapassado.visibility = View.VISIBLE
        else
            tv_aviso_valor_ultrapassado.visibility = View.GONE

        listview_registro_item_carteira.adapter = RegistroItemCarteiraAdapter(this, item.registros)

        if (item.registros.size > 0)
            tv_sem_registros_item.visibility = View.GONE
        else
            tv_sem_registros_item.visibility = View.VISIBLE
    }

    private fun marcarComoPago() {

    }

    private fun calcularValorPago(registros: ArrayList<RegistroItem>): BigDecimal {
        var soma = BigDecimal.ZERO
        for (registro in registros) {
            soma = soma.add(registro.valor)
        }
        return soma
    }

    @SuppressLint("InflateParams")
    private fun createDialogNovoAjuste() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_element, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.dialog_add_element_button_adicionar.setOnClickListener {
            if (dialogView.textinput_valor.text.toString() == ""
                || dialogView.textinput_descricao.text.toString() == ""
            ) {
                dialogView.textinput_error.visibility = View.VISIBLE
            } else {
                val novoRegistro = RegistroItem()
                novoRegistro.descricao = dialogView.textinput_descricao.text.toString()
                novoRegistro.valor = BigDecimal(dialogView.textinput_valor.text.toString())
                novoRegistro.carteiraPendencia = item

                registroItemDAO.inserir(novoRegistro)

                dialog.dismiss()
                Toast.makeText(this, R.string.toast_detalhe_item_carteira_registro_adicionado, Toast.LENGTH_LONG).show()

                refreshDados()
            }
        }

        dialog.show()
    }

    private fun createDialogExcluir(item: RegistroItem): Boolean {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.all_dialog_title_excluir))
            .setMessage(getString(R.string.all_dialog_message_excluir))
            .setPositiveButton(R.string.all_string_sim) { _, _ ->
                registroItemDAO.excluir(item)
                Toast.makeText(
                    this@DetalhesCarteiraPendenciaActivity,
                    R.string.toast_detalhe_item_carteira_registro_removido,
                    Toast.LENGTH_SHORT
                ).show()
                refreshDados()
            }
            .setNegativeButton(R.string.all_string_nao, null)
            .show()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
