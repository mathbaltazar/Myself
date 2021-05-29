package com.baltazarstudio.regular.ui.despesa

import android.graphics.Rect
import android.os.Bundle
import android.transition.Slide
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.DespesaContext
import com.baltazarstudio.regular.context.RegistroContext
import com.baltazarstudio.regular.model.Despesa
import com.baltazarstudio.regular.model.exception.ModelException
import com.baltazarstudio.regular.observer.Events
import com.baltazarstudio.regular.observer.Trigger
import com.baltazarstudio.regular.ui.adapter.RegistrosAdapterSection
import com.baltazarstudio.regular.util.CurrencyMask
import com.baltazarstudio.regular.util.Utils
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_detalhes_despesa.*


class DetalhesDespesaActivity : AppCompatActivity() {
    
    companion object {
        private const val SEM_VENCIMENTO: String = "Sem vencimento"
        private const val MENU_ITEMID_EXCLUIR: Int = 0
    }
    
    private lateinit var despesa: Despesa
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_despesa)
        supportActionBar?.title = "Detalhes"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        despesa = DespesaContext.despesaDetalhada!!
        setUpView()
        bindView()
        registerCallbacks()
        setUpTransitionAnimation()
    }
    
    private fun registerCallbacks() {
        Trigger.watcher().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { t ->
                when (t) {
                    is Events.UpdateDespesas -> carregarRegistros()
                }
            }.apply {  }
    }
    
    private fun setUpView() {
        
        et_detalhes_despesa_nome.apply {
            setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    try {
                        atualizarNomeDespesa(text.toString())
                        til_detalhes_despesa_nome.error = null
                    } catch (e: ModelException) {
                        til_detalhes_despesa_nome.error = e.message
                    }
                }
            }
        }
        
        et_detalhes_despesa_valor.apply {
            addTextChangedListener(CurrencyMask(this))
            setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    try {
                        atualizarValorDespesa(text.toString())
                        til_detalhes_despesa_valor.error = null
                    } catch (e: ModelException) {
                        til_detalhes_despesa_valor.error = e.message
                    }
                } else {
                    (v as TextInputEditText).setSelection(v.length())
                }
            }
        }
        
        et_detalhes_despesa_vencimento.setAdapter(obterAdapter())
        et_detalhes_despesa_vencimento.setOnItemClickListener { _, _, position, _ ->
            atualizarDiaVencimento(position)
        }
    
        button_detalhes_despesa_adicionar_registro.setOnClickListener {
            val dialog = RegistrarDespesaDialog(this, despesa)
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
        
        carregarRegistros()
    }
    
    private fun setUpTransitionAnimation() {
        val slide = Slide()
        slide.slideEdge = Gravity.END
        slide.duration = 400
        slide.interpolator = AccelerateDecelerateInterpolator()
        window.exitTransition = slide
        window.enterTransition = slide
    }
    
    private fun carregarRegistros() {
        val registros = RegistroContext.getDAO(this).getRegistrosFiltradosPelaDespesa(despesa.codigo)
    
        if (registros.isNotEmpty()) {
            tv_detalhes_despesa_sem_registros.visibility = View.GONE
            rv_detalhes_despesa_registros.visibility = View.VISIBLE
            
            val adapter = SectionedRecyclerViewAdapter()
    
            for (ano in Utils.getAnosDisponiveis(registros)) {
                for (mes in Utils.getMesDisponivelPorAno(registros, ano)) {
                    val itens = Utils.filtrarItensPorData(registros, mes, ano)
            
                    if (itens.isEmpty()) continue
            
                    val section = RegistrosAdapterSection(adapter, ano, mes, itens)
                    section.disableMultiSelectMode()
                    
                    adapter.addSection(section)
                }
            }
    
            rv_detalhes_despesa_registros.adapter = adapter
            rv_detalhes_despesa_registros.layoutManager = LinearLayoutManager(this)
        } else {
            tv_detalhes_despesa_sem_registros.visibility = View.VISIBLE
            rv_detalhes_despesa_registros.visibility = View.GONE
        }
    }
    
    @Throws(ModelException::class)
    private fun atualizarNomeDespesa(nome: String) {
        despesa.nome = nome
        DespesaContext.getDAO(this).atualizarNome(despesa)
    }
    
    @Throws(ModelException::class)
    private fun atualizarValorDespesa(valor: String) {
        despesa.valor = Utils.unformatCurrency(valor).toDouble()
        DespesaContext.getDAO(this).atualizarValor(despesa)
    }
    
    private fun atualizarDiaVencimento(dia: Int) {
        despesa.diaVencimento = dia
        DespesaContext.getDAO(this).atualizarDiaVencimento(despesa)
    }
    
    private fun obterAdapter(): ArrayAdapter<String> {
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item)
    
        adapter.add(SEM_VENCIMENTO)
        
        (1..28).forEach { dia -> adapter.add("$dia") }
    
        return adapter
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(Menu.NONE, MENU_ITEMID_EXCLUIR, Menu.NONE, "Excluir")
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
    
    private fun excluirDepesa() {
        AlertDialog.Builder(this).setTitle("Excluir")
            .setMessage("Deseja realmente deletar esta despesa?")
            .setPositiveButton("Excluir") { _, _ ->
                DespesaContext.getDAO(this).deletar(despesa)
                Trigger.launch(Events.Toast("Removido!"), Events.UpdateDespesas())
                finish()
            }.setNegativeButton("Cancelar", null)
            .show()
    }
    
    override fun onDestroy() {
        Trigger.launch(Events.UpdateDespesas())
        super.onDestroy()
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
}
