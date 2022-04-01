package br.com.myself.ui.financas.despesa

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.R
import br.com.myself.context.DespesaContext
import br.com.myself.context.RegistroContext
import br.com.myself.model.Despesa
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.ui.adapter.RegistrosAdapterSection
import br.com.myself.util.Utils
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_detalhes_despesa.*
import java.math.BigDecimal


class DetalhesDespesaActivity : AppCompatActivity() {
    
    companion object {
        private const val MENU_ITEMID_EXCLUIR: Int = 0
    }
    
    private val despesa: Despesa = DespesaContext.despesasDataView.despesaDetalhada!!
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_despesa)
        supportActionBar?.title = "Detalhes"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpView()
        bindView()
    }
    
    private fun setUpView() {
        et_detalhes_despesa_vencimento.setAdapter(obterAdapter())
        et_detalhes_despesa_vencimento.setOnItemClickListener { _, _, position, _ ->
            despesa.diaVencimento = position
        }
    
        button_detalhes_despesa_adicionar_registro.setOnClickListener {
            val dialog =
                RegistrarDespesaDialog(this, despesa)
            dialog.setOnDismissListener {
                carregarRegistros()
            }
            dialog.show()
        }
    }
    
    private fun bindView() {
        et_detalhes_despesa_nome.setText(despesa.nome)
        et_detalhes_despesa_nome.setSelection(despesa.nome!!.length)
        et_detalhes_despesa_valor.setText(Utils.formatCurrency(despesa.valor))
        
        with(et_detalhes_despesa_vencimento) {
            setText(adapter.getItem(despesa.diaVencimento).toString(), false)
        }
        
        rv_detalhes_despesa_registros.adapter = SectionedRecyclerViewAdapter()
        rv_detalhes_despesa_registros.layoutManager = LinearLayoutManager(this)
        
        carregarRegistros()
    }
    
    private fun carregarRegistros() {
        val registros = RegistroContext.getDAO(this).getRegistrosFiltradosPelaDespesa(despesa.id)
        
        if (registros.isEmpty()) {
            tv_detalhes_despesa_sem_registros.visibility = View.VISIBLE
            rv_detalhes_despesa_registros.visibility = View.GONE
        } else {
            tv_detalhes_despesa_sem_registros.visibility = View.GONE
            rv_detalhes_despesa_registros.visibility = View.VISIBLE
    
            val mesesPossiveis = registros.map { it.referencia_mes_ano }.distinct()
    
            val adapter = rv_detalhes_despesa_registros.adapter as SectionedRecyclerViewAdapter
            adapter.removeAllSections()
            adapter.notifyDataSetChanged()
            
            mesesPossiveis.forEach {
                val section = RegistrosAdapterSection(
                    adapter,
                    registros.filter { registro -> registro.referencia_mes_ano == it },
                    it!!
                )
                adapter.addSection(section)
            }
        }
    }
    
    private fun obterAdapter(): ArrayAdapter<String> {
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item)
    
        adapter.add("Sem vencimento")
        
        (1..28).forEach { dia -> adapter.add("$dia") }
    
        return adapter
    }
    
    private fun excluirDepesa() {
        var mensagem = "Nome: ${despesa.nome}"
        mensagem += "\nValor: ${Utils.formatCurrency(despesa.valor)}"
        if (despesa.diaVencimento != 0) mensagem += "\nVencimento: ${despesa.diaVencimento}"
    
        AlertDialog.Builder(this).setTitle("Excluir despesa?")
            .setMessage(mensagem)
            .setPositiveButton("Excluir") { _, _ ->
                DespesaContext.getDAO(this).deletar(despesa)
                DespesaContext.removerDespesa(despesa)
                Trigger.launch(Events.Toast("Removido!"), Events.UpdateDespesas())
                finish()
            }.setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun persistirDespesa(): Boolean {
        val nome = et_detalhes_despesa_nome.text.toString()
        val valor = Utils.unformatCurrency(et_detalhes_despesa_valor.text.toString()).toDouble()
        
        if (nome.isBlank()) {
            et_detalhes_despesa_nome.requestFocus()
            Trigger.launch(Events.Toast("Nome inválido"))
            return false
        }
        
        if (valor.toBigDecimal() <= BigDecimal.ZERO) {
            et_detalhes_despesa_valor.requestFocus()
            Trigger.launch(Events.Toast("Valor inválido"))
            return false
        }
        
        this.despesa.nome = nome
        this.despesa.valor = valor
        // Vencimento já é atribuído na seleção, validação desnecessária
        DespesaContext.getDAO(this).alterar(this.despesa)
        Trigger.launch(Events.UpdateDespesas())
        
        return true
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(Menu.NONE,
            MENU_ITEMID_EXCLUIR, Menu.NONE, "Excluir")
        return super.onCreateOptionsMenu(menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        } else if (item.itemId == MENU_ITEMID_EXCLUIR) {
            excluirDepesa()
        }
        
        return super.onOptionsItemSelected(item)
    }
    
    override fun onBackPressed() {
        if (persistirDespesa()) {
            super.onBackPressed()
        }
    }
    
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            
            if (view is TextInputEditText || view is MaterialAutoCompleteTextView) { // Edittext perde o foco quando tocado em outro lugaar
                val rect = Rect()
                view.getGlobalVisibleRect(rect)
                
                if (!rect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    view.clearFocus()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
    
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
}
