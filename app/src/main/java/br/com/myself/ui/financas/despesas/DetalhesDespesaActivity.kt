package br.com.myself.ui.financas.despesas

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.R
import br.com.myself.databinding.ActivityDetalhesDespesaBinding
import br.com.myself.domain.entity.Registro
import br.com.myself.ui.adapter.RegistroAdapter
import br.com.myself.util.AdapterClickListener
import br.com.myself.util.CurrencyMask
import br.com.myself.util.Utils
import br.com.myself.viewmodel.DetalhesDespesaActivityViewModel
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import org.jetbrains.anko.toast
import java.math.BigDecimal

class DetalhesDespesaActivity : AppCompatActivity() {
    
    companion object {
        const val DESPESA_ID: String = "DESPESA_ID"
        private const val MENU_ITEMID_EXCLUIR: Int = 0
        private const val MENU_ITEMID_SALVAR: Int = 1
    }
    
    private val viewModel: DetalhesDespesaActivityViewModel by viewModels()
    private val binding by lazy { ActivityDetalhesDespesaBinding.inflate(layoutInflater) }
    private var menuItemSalvar: MenuItem? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = "Detalhes"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
        
        
        val despesaId = intent.getLongExtra(DESPESA_ID, 0)
    
        viewModel.loadDespesa(despesaId) {
            setUpView()
            setUpObservers()
        }
    }
    
    private fun setUpView() {
        with(binding.textinputNome) {
            setText(viewModel.despesa.nome)
            setSelection(viewModel.despesa.nome.length)
            addTextChangedListener { viewModel.setDespesaEdited() }
        }
        
        with(binding.textinputValor) {
            addTextChangedListener(CurrencyMask(this))
            setText(Utils.formatCurrency(viewModel.despesa.valor))
            addTextChangedListener { viewModel.setDespesaEdited() }
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) setSelection(length())
            }
        }
        
        with(binding.dropdownVencimento) {
            val vencAdapter = obterVencimentoAdapter()
            setAdapter(vencAdapter)
            setText(vencAdapter.getItem(viewModel.despesa.diaVencimento), false)
            setOnItemClickListener { _, _, position, _ ->
                viewModel.despesa.diaVencimento = position
                viewModel.setDespesaEdited()
            }
        }
    
        binding.buttonRegistrar.setOnClickListener {
            viewModel.getSugestoes { sugestoes ->
                val dialog =
                    RegistrarDespesaDialog(viewModel.despesa, sugestoes) { dialog, valor, data ->
                        viewModel.registrar(valor, data) {
                            dialog.dismiss()
                        }
                    }
                dialog.show(supportFragmentManager, null)
            }
        }
    
        setUpRegistrosAdapter()
    }
    
    private fun setUpObservers() {
        viewModel.registrosDaDespesa.observe(this) { registros ->
            binding.textviewSemRegistros.visibility =
                if (registros.isEmpty()) View.VISIBLE else View.GONE
        
            (binding.recyclerviewRegistros.adapter as RegistroAdapter).submitList(registros)
        }
        
        viewModel.despesaEdited.observe(this) {
            menuItemSalvar?.isEnabled = it
        }
    }
    
    private fun setUpRegistrosAdapter() {
        val adapter = RegistroAdapter()
        adapter.setClickListener(AdapterClickListener(
            onLongClick = { registro ->
                confirmarExcluirRegistro(registro)
            }))
        binding.recyclerviewRegistros.adapter = adapter
        binding.recyclerviewRegistros.layoutManager = LinearLayoutManager(this)
    }
    
    private fun confirmarExcluirRegistro(registro: Registro) {
        var msg = "Descrição: ${registro.descricao}"
        msg += "\nValor: ${Utils.formatCurrency(registro.valor)}"
    
        AlertDialog.Builder(this).setTitle("Excluir registro?").setMessage(msg)
            .setPositiveButton("Excluir") { _, _ ->
                viewModel.excluirRegistro(registro, onDeleted = { toast("Excluído!") })
            }.setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun obterVencimentoAdapter(): ArrayAdapter<String> {
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item)
    
        adapter.add("Sem vencimento")
        
        (1..28).forEach { dia -> adapter.add("$dia") }
    
        return adapter
    }
    
    private fun confirmarExcluirDepesa() {
        var msg = "Nome: ${viewModel.despesa.nome}"
        msg += "\nValor: ${Utils.formatCurrency(viewModel.despesa.valor)}"
        if (viewModel.despesa.diaVencimento != 0) msg += "\nVencimento: ${viewModel.despesa.diaVencimento}"
    
        AlertDialog.Builder(this).setTitle("Excluir despesa")
            .setMessage(msg)
            .setPositiveButton("Excluir") { _, _ ->
               viewModel.excluirDespesa(onDeleted = {
                   toast("Removido!")
                   finish()
               })
            }.setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun salvarDespesa(finishOnSave: Boolean = false) {
        val nome = binding.textinputNome.text.toString()
        val valor = Utils.unformatCurrency(binding.textinputValor.text.toString()).toBigDecimal()
        
        if (nome.isBlank()) {
            binding.textinputNome.requestFocus()
            toast("Nome inválido")
            return
        }
        
        if (valor <= BigDecimal.ZERO) {
            binding.textinputValor.requestFocus()
            toast("Valor inválido")
            return
        }
        
        viewModel.despesa.nome = nome
        viewModel.despesa.valor = valor.toDouble()
        // Vencimento já é atribuído na seleção, validação desnecessária
        
        viewModel.salvarDespesa {
            if (finishOnSave) finish()
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(Menu.NONE,
            MENU_ITEMID_EXCLUIR, Menu.NONE, "Excluir")
            .setIcon(R.drawable.ic_delete)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
    
        menu.add(Menu.NONE,
            MENU_ITEMID_SALVAR, Menu.NONE, "Salvar")
            .setEnabled(false)
            .setIcon(R.drawable.ic_check)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        
        menuItemSalvar = menu.findItem(MENU_ITEMID_SALVAR)
        
        return super.onCreateOptionsMenu(menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            MENU_ITEMID_SALVAR -> salvarDespesa()
            MENU_ITEMID_EXCLUIR -> confirmarExcluirDepesa()
        }
        return super.onOptionsItemSelected(item)
    }
    
    override fun onBackPressed() {
        if (viewModel.wasEdited()) confirmarSalvarDespesaOnClose()
        else super.onBackPressed()
    }
    
    private fun confirmarSalvarDespesaOnClose() {
        AlertDialog.Builder(this)
            .setMessage("Salvar dados?")
            .setPositiveButton("Salvar") { _, _ ->
                salvarDespesa(finishOnSave = true)
            }
            .setNegativeButton("Não Salvar") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
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
