package com.baltazarstudio.regular.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.database.dao.PendenciaDAO
import com.baltazarstudio.regular.model.Pendencia
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.activity_detalhes_pendencia.*

class DetalhesPendenciaActivity : AppCompatActivity() {

    lateinit var item: Pendencia

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_pendencia)
        supportActionBar?.title = getString(R.string.activity_title_detalhes_pendencia)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        button_detalhes_pendencia_add_nota.setOnClickListener {
            // TODO Adicionar nota
        }

        button_detalhes_item_carteira_marcar_como_pago.setOnClickListener {
            marcarComoPago()
        }

    }


    private fun refreshPendencia() {
        item = PendenciaDAO(this).get(intent.getIntExtra("id", 0))

        tv_item_pendencia_valor.text = Utils.formatCurrency(item.valor)
        tv_item_carteira_data.text = item.data
        tv_item_pendencia_descricao.text = item.descricao

        if (item.notas.size > 0)
            label_detalhes_pendecia_sem_notas.visibility = View.GONE
        else
            label_detalhes_pendecia_sem_notas.visibility = View.VISIBLE
    }

    private fun marcarComoPago() {
        AlertDialog.Builder(this)
                .setTitle(R.string.all_string_confirmar)
                .setMessage(R.string.dialog_mensagem_detalhe_pendencia_pago)
                .setPositiveButton(R.string.all_string_sim) { _: DialogInterface, _: Int ->
                    PendenciaDAO(this).definirComoPago(item)
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
