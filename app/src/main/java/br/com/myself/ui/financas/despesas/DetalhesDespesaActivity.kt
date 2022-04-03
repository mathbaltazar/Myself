package br.com.myself.ui.financas.despesas

import android.content.Context
import android.content.DialogInterface
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
import br.com.myself.model.entity.Despesa
import br.com.myself.model.repository.RegistroRepository
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.ui.adapter.RegistroAdapter
import br.com.myself.util.AdapterClickListener
import br.com.myself.util.Async
import br.com.myself.util.CurrencyMask
import br.com.myself.util.Utils
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_detalhes_despesa.*
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import org.jetbrains.anko.toast
import java.math.BigDecimal


class DetalhesDespesaActivity : AppCompatActivity() {
    
    companion object {
        private const val MENU_ITEMID_EXCLUIR: Int = 0
    }
    
    private val despesa: Despesa = DespesaContext.getDataView(this).despesaDetalhada!!
    
    private val registroRepository = RegistroRepository(this)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_despesa)
        supportActionBar?.title = "Detalhes"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpView()
        bindView()
    }
    
    private fun setUpView() {
        with(et_detalhes_despesa_valor) {
            addTextChangedListener(CurrencyMask(this))
            onFocusChange { _, hasFocus ->
                if (hasFocus) setSelection(length())
            }
        }
        
        et_detalhes_despesa_vencimento.setAdapter(obterAdapter())
        et_detalhes_despesa_vencimento.setOnItemClickListener { _, _, position, _ ->
            despesa.diaVencimento = position
        }
    
        button_detalhes_despesa_adicionar_registro.setOnClickListener {
            val dialog = RegistrarDespesaDialog(despesa, registroRepository)
            dialog.onDismiss(object : DialogInterface {
                override fun dismiss() { carregarRegistros() }
                override fun cancel() {}
            })
            dialog.show(supportFragmentManager, null)
        }
    }
    
    private fun bindView() {
        et_detalhes_despesa_nome.setText(despesa.nome)
        et_detalhes_despesa_nome.setSelection(despesa.nome!!.length)
       
        et_detalhes_despesa_valor.setText(Utils.formatCurrency(despesa.valor))
    
        with(et_detalhes_despesa_vencimento) {
            setText(adapter.getItem(despesa.diaVencimento).toString(), false)
        }
        
        
        val adapter = RegistroAdapter()
        adapter.setClickListener(AdapterClickListener(
            onLongClick = { registro ->
                var msg = "Descrição: ${registro.descricao}"
                msg += "\nValor: ${Utils.formatCurrency(registro.valor)}"
        
                AlertDialog.Builder(this).setTitle("Excluir registro?").setMessage(msg)
                    .setPositiveButton("Excluir") { _, _ ->
                        Async.doInBackground({ registroRepository.excluirRegistro(registro) }, {
                            toast("Excluído!")
                            Trigger.launch(Events.UpdateRegistros)
                    
                            carregarRegistros()
                        })
                    }.setNegativeButton("Cancelar", null).show()
            }))
        rv_detalhes_despesa_registros.adapter = adapter
        rv_detalhes_despesa_registros.layoutManager = LinearLayoutManager(this)
        
        carregarRegistros()
    }
    
    private fun carregarRegistros() {
        Async.doInBackground({ registroRepository.pesquisarRegistros(despesa.id) }, { registros ->
    
    
            tv_detalhes_despesa_sem_registros.visibility =
                if (registros.isEmpty()) View.VISIBLE else View.GONE
            
            (rv_detalhes_despesa_registros.adapter as RegistroAdapter).submitList(registros)
            
            
        })
    }
    
    private fun obterAdapter(): ArrayAdapter<String> {
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item)
    
        adapter.add("Sem vencimento")
        
        (1..28).forEach { dia -> adapter.add("$dia") }
    
        return adapter
    }
    
    private fun excluirDepesa() {
        var msg = "Nome: ${despesa.nome}"
        msg += "\nValor: ${Utils.formatCurrency(despesa.valor)}"
        if (despesa.diaVencimento != 0) msg += "\nVencimento: ${despesa.diaVencimento}"
    
        AlertDialog.Builder(this).setTitle("Excluir despesa")
            .setMessage(msg)
            .setPositiveButton("Excluir") { _, _ ->
                DespesaContext.getDAO(this).deletar(despesa)
                DespesaContext.removerDespesa(despesa)
                Trigger.launch(Events.Toast("Removido!"), Events.UpdateDespesas)
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
        Trigger.launch(Events.UpdateDespesas)
        
        return true
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(Menu.NONE,
            MENU_ITEMID_EXCLUIR, Menu.NONE, "Excluir")
            .setIcon(R.drawable.ic_delete)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
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
        // TODO View.clearFocus() não está funcionando
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
