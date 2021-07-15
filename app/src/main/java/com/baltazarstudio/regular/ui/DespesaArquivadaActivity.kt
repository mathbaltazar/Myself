package com.baltazarstudio.regular.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.DespesaContext
import com.baltazarstudio.regular.model.Despesa
import com.baltazarstudio.regular.model.enum.Boolean
import com.baltazarstudio.regular.ui.adapter.DespesasArquivadasAdapter
import kotlinx.android.synthetic.main.activity_despesa_arquivada.*

class DespesaArquivadaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_despesa_arquivada)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Arquivadas"
        
        carregarDespesasArquivadas()
    }
    
    private fun carregarDespesasArquivadas() {
        val dao = DespesaContext.getDAO(this)
        val despesas = dao.getTodasDespesas().filter { it.arquivado == Boolean.TRUE }.toMutableList()
    
        if (despesas.isEmpty()) {
            tv_despesa_arquivada_nenhum.visibility = View.VISIBLE
        } else {
            tv_despesa_arquivada_nenhum.visibility = View.GONE
        
            rv_despesas_arquivadas.layoutManager = LinearLayoutManager(this)
            val adapter = DespesasArquivadasAdapter(this, despesas)
            adapter.setOnStateChangedListener(object : DespesasArquivadasAdapter.OnStateChangedListener {
                override fun onChanged(despesa: Despesa) {
                    carregarDespesasArquivadas()
                }
            })
            rv_despesas_arquivadas.adapter = adapter
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): kotlin.Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}