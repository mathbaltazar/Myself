package com.baltazarstudio.regular.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.adapter.ItemCarteiraRegistroAdapter
import com.baltazarstudio.regular.database.ItemCarteiraAbertaDAO
import com.baltazarstudio.regular.model.ItemCarteiraAberta
import com.baltazarstudio.regular.model.RegistroItemCarteira
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.activity_detalhes_item_carteira.*
import java.math.BigDecimal

class DetalhesItemCarteiraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_item_carteira)
        supportActionBar?.title = getString(R.string.activity_title_detalhes_item_carteira)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        init()
    }

    private fun init() {
        val item = ItemCarteiraAbertaDAO(this).get(intent.getIntExtra("id", 0))

        tv_item_carteira_valor.text = Utils.formatCurrency(item.valor)
        tv_item_carteira_data.text = item.data
        tv_item_carteira_descricao.text = item.descricao
        tv_item_carteira_valor_pago.text = calcularValorPago(item.registros)

        if (item.registros.size > 0) {
            tv_sem_registros_item.visibility = View.GONE
            listview_item_carteira_registro.adapter = ItemCarteiraRegistroAdapter(this, item.registros)
        } else {
            tv_sem_registros_item.visibility = View.VISIBLE
        }
    }

    private fun calcularValorPago(registros: ArrayList<RegistroItemCarteira>): String {
        return Utils.formatCurrency(BigDecimal.ZERO)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
