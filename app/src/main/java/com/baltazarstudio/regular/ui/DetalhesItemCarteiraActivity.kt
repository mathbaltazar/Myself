package com.baltazarstudio.regular.ui

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.adapter.RegistroItemCarteiraAdapter
import com.baltazarstudio.regular.database.CarteiraPendenciaDAO
import com.baltazarstudio.regular.database.RegistroItemDAO
import com.baltazarstudio.regular.model.CarteiraPendencia
import com.baltazarstudio.regular.model.RegistroItem
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.activity_detalhes_item_carteira.*
import kotlinx.android.synthetic.main.dialog_detalhe_item_carteira_add_registro.view.*
import java.math.BigDecimal

class DetalhesItemCarteiraActivity : AppCompatActivity() {

    lateinit var item: CarteiraPendencia

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_item_carteira)
        supportActionBar?.title = getString(R.string.activity_title_detalhes_item_carteira)
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

        initializeItemCarteira()
    }

    private fun initializeItemCarteira() {
        item = CarteiraPendenciaDAO(this).get(intent.getIntExtra("id", 0))

        tv_item_carteira_valor.text = Utils.formatCurrency(item.valor)
        tv_item_carteira_data.text = item.data
        tv_item_carteira_descricao.text = item.descricao

        val valorPago = calcularValorPago(item.registros)
        tv_item_carteira_valor_pago.text = Utils.formatCurrency(valor = valorPago)


        if (valorPago.compareTo(item.valor) > 0)
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
        val dialogView = layoutInflater.inflate(R.layout.dialog_detalhe_item_carteira_add_registro, null)
        val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

        dialogView.button_inserir_item_registro.setOnClickListener {
            if (dialogView.textinput_item_registro_valor.text.toString() == ""
                    || dialogView.textinput_item_registro_descricao.text.toString() == "") {
                dialogView.textinput_item_registro_error.visibility = View.VISIBLE
            } else {
                val novoRegistro = RegistroItem()
                novoRegistro.descricao = dialogView.textinput_item_registro_descricao.text.toString()
                novoRegistro.valor = BigDecimal(dialogView.textinput_item_registro_valor.text.toString())
                novoRegistro.carteiraPendencia = item

                RegistroItemDAO(this).inserir(novoRegistro)

                dialog.dismiss()
                Toast.makeText(this, R.string.toast_detalhe_item_carteira_registro_adicionado, Toast.LENGTH_LONG).show()

                initializeItemCarteira()
            }
        }

        dialog.show()
    }

    private fun createDialogExcluir(item: RegistroItem): Boolean {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.all_dialog_title_excluir))
                .setMessage(getString(R.string.all_dialog_message_excluir))
                .setPositiveButton(R.string.all_string_sim, object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        RegistroItemDAO(this@DetalhesItemCarteiraActivity).excluir(item)
                        Toast.makeText(this@DetalhesItemCarteiraActivity, R.string.all_toast_detalhe_item_carteira_registro_removido, Toast.LENGTH_SHORT).show()
                        initializeItemCarteira()
                    }
                })
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
