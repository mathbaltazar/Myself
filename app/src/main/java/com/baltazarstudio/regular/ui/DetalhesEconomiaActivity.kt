package com.baltazarstudio.regular.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.database.dao.EconomiaDAO
import com.baltazarstudio.regular.model.Economia
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.activity_detalhes_economia.*

class DetalhesEconomiaActivity : AppCompatActivity() {

    private lateinit var item: Economia

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_economia)
        supportActionBar?.title = getString(R.string.activity_title_detalhes_economia)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        refreshDados()

    }

    private fun refreshDados() {
        item = EconomiaDAO(this).get(intent.getIntExtra("id", 0))

        tv_detalhes_economia_descricao.text = item.descricao
        tv_detalhes_economia_data.text = item.data
        tv_detalhes_economia_valor.text = Utils.formatCurrency(item.valor).replace("R$", "").trim()
        tv_detalhes_economia_valor_poupanca.text = Utils.formatCurrency(item.valorPoupanca).replace("R$", "").trim()


        if (item.valorPoupanca!! < item.valor) {
            tv_detalhes_economia_aviso_valor_atingido.visibility = View.GONE
        } else {
            tv_detalhes_economia_aviso_valor_atingido.visibility = View.VISIBLE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
