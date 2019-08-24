package com.baltazarstudio.regular.ui

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.adapter.NotasAdapter
import com.baltazarstudio.regular.database.dao.PendenciaDAO
import com.baltazarstudio.regular.model.Pendencia
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.activity_detalhes_pendencia.*

class DetalhesPendenciaActivity : AppCompatActivity() {

    private lateinit var item: Pendencia
    private val pendenciaDAO = PendenciaDAO(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_pendencia)
        supportActionBar?.title = getString(R.string.activity_title_detalhes_pendencia)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        imagebutton_nota_cancelar.setOnClickListener {
            cardview_add_nota.visibility = View.GONE
            textinput_add_nota.error = null
            textinput_add_nota.text = null

            if (item.notas.isEmpty()) {
                label_detalhes_pendecia_sem_notas.visibility = View.VISIBLE
            }

            Utils.hideKeyboard(this, textinput_add_nota)
        }


        button_detalhes_pendencia_add_nota.setOnClickListener {
            cardview_add_nota.visibility = View.VISIBLE
            label_detalhes_pendecia_sem_notas.visibility = View.GONE
            textinput_add_nota.requestFocus()
            Utils.showKeyboard(this, textinput_add_nota)
        }

        textinput_add_nota.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                adicionarNota()
            }
            true
        }

        button_detalhes_item_carteira_marcar_como_pago.setOnClickListener {
            marcarComoPago()
        }
    }

    private fun adicionarNota() {
        if (textinput_add_nota.text.isNullOrBlank()) {
            textinput_add_nota.error = "Não pode estar vazio"
        } else {
            item.notas.add(0, textinput_add_nota.text.toString())
            pendenciaDAO.atualizarNotas(item)
            Toast.makeText(this, R.string.toast_detalhes_pendencia_nota_adicionada, Toast.LENGTH_SHORT).show()

            imagebutton_nota_cancelar.callOnClick()
            explodNotas()
        }
    }

    private fun refreshPendencia() {
        item = pendenciaDAO.get(intent.getIntExtra("id", 0))

        tv_item_pendencia_valor.text = Utils.formatCurrency(item.valor)
        tv_item_carteira_data.text = item.data
        tv_item_pendencia_descricao.text = item.descricao

        explodNotas()
    }

    private fun explodNotas() {
        listview_detalhes_pendencia_notas.adapter = NotasAdapter(this, item.notas)

        if (item.notas.isNotEmpty()) {
            label_detalhes_pendecia_sem_notas.visibility = View.GONE
        } else {
            label_detalhes_pendecia_sem_notas.visibility = View.VISIBLE
        }
    }


    fun excluirNota(nota: String) {
        item.notas.remove(nota)
        pendenciaDAO.atualizarNotas(item)
        explodNotas()
    }

    private fun marcarComoPago() {
        AlertDialog.Builder(this)
                .setTitle(R.string.all_string_confirmar)
                .setMessage(R.string.dialog_mensagem_detalhe_pendencia_pago)
                .setPositiveButton(R.string.all_string_sim) { _: DialogInterface, _: Int ->
                    pendenciaDAO.definirComoPago(item)
                    Toast.makeText(applicationContext, R.string.toast_detalhes_pendencia_pago, Toast.LENGTH_LONG).show()
                    finish()
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

    override fun onResume() {
        super.onResume()
        refreshPendencia()
    }
}
