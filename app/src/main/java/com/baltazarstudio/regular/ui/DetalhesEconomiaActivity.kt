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
import com.baltazarstudio.regular.database.dao.EconomiaDAO
import com.baltazarstudio.regular.model.Economia
import com.baltazarstudio.regular.util.CurrencyMask
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.activity_detalhes_economia.*
import kotlinx.android.synthetic.main.dialog_ajuste_poupanca.view.*
import java.math.BigDecimal

class DetalhesEconomiaActivity : AppCompatActivity() {

    private lateinit var item: Economia
    private var economiaDAO = EconomiaDAO(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_economia)
        supportActionBar?.title = getString(R.string.activity_title_detalhes_economia)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        button_ajustar_poupanca.setOnClickListener {
            createDialogAjustarPoupanca()
        }

        button_detalhes_economia_conquistado.setOnClickListener {
            definirEconomiaConquistada()
        }

        updateDados()
    }

    private fun updateDados() {
        item = economiaDAO.get(intent.getIntExtra("id", 0))

        tv_detalhes_economia_descricao.text = item.descricao
        tv_detalhes_economia_data.text = item.data
        tv_detalhes_economia_valor.text = Utils.formatCurrency(item.valor).replace("R$", "").trim()
        tv_detalhes_economia_valor_poupanca.text = Utils.formatCurrency(item.valorPoupanca).replace("R$", "").trim()

        if (item.valorPoupanca < item.valor) {
            tv_detalhes_economia_aviso_valor_atingido.visibility = View.GONE
        } else {
            tv_detalhes_economia_aviso_valor_atingido.visibility = View.VISIBLE
        }
    }

    @SuppressLint("InflateParams")
    private fun createDialogAjustarPoupanca() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_ajuste_poupanca, null)
        val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

        dialogView.textinput_ajuste_valor_poupanca.addTextChangedListener(
                CurrencyMask(dialogView.textinput_ajuste_valor_poupanca)
        )

        dialogView.button_dialog_ajuste_poupanca_confirmar.setOnClickListener {
            val valor = dialogView.textinput_ajuste_valor_poupanca.text.toString()
            val valorDecimal = Utils.unformatCurrency(valor).toBigDecimal()

            if (valorDecimal > BigDecimal.ZERO) {
                if (dialogView.tab_ajuste_poupanca_movimento.selectedTabPosition == 0) {
                    ajustarPoupanca(valorDecimal, false)
                    dialog.dismiss()
                } else {
                    if (valorDecimal <= item.valorPoupanca) {
                        ajustarPoupanca(valorDecimal, true)
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, R.string.dialog_ajuste_poupanca_valor_invalido, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, R.string.dialog_ajuste_poupanca_maior_que_zero, Toast.LENGTH_LONG).show()
            }
        }

        dialogView.label_dialog_ajuste_poupanca_valor_maximo_retirada.text =
                String.format("Valor máximo para retirada: %s", Utils.formatCurrency(item.valorPoupanca))

        dialog.show()
    }

    private fun ajustarPoupanca(valor: BigDecimal, retirada: Boolean) {
        if (retirada) {
            economiaDAO.retirarPoupanca(item, valor)
        } else {
            economiaDAO.adicionarPoupanca(item, valor)
        }

        Toast.makeText(this, R.string.detalhes_economia_valor_poupanca_registrado, Toast.LENGTH_SHORT).show()
        updateDados()
    }

    private fun definirEconomiaConquistada() {
        AlertDialog.Builder(this)
                .setTitle(R.string.all_string_confirmar)
                .setMessage(R.string.dialog_mensagem_detalhe_economia_conquistado)
                .setPositiveButton(R.string.all_string_sim) { _: DialogInterface, _: Int ->
                    economiaDAO.definirEconomiaConquistada(item)
                    Toast.makeText(this@DetalhesEconomiaActivity,
                            R.string.toast_detalhes_economia_conquistada,
                            Toast.LENGTH_LONG).show()
                    //finish()
                }
                .setNegativeButton(R.string.all_string_nao) { _: DialogInterface, _: Int -> }
                .create()
                .show()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
