package br.com.myself.ui.financas.registros

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.databinding.ActivityPesquisarRegistrosBinding
import br.com.myself.data.model.Registro
import br.com.myself.ui.adapter.RegistroAdapter
import br.com.myself.util.AdapterClickListener
import br.com.myself.util.Utils
import br.com.myself.viewmodel.PesquisarRegistrosActivityViewModel
import io.github.inflationx.viewpump.ViewPumpContextWrapper

class PesquisarRegistrosActivity : AppCompatActivity() {
    
    private val viewModel: PesquisarRegistrosActivityViewModel by viewModels()
    private val binding by lazy { ActivityPesquisarRegistrosBinding.inflate(layoutInflater) }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        
        configureAdapter()
    
        binding.textInputLayoutBusca.apply {
            setEndIconOnClickListener {
                iniciarBusca(editText?.text.toString().trim())
            }
            editText?.setOnEditorActionListener { v, _, _ ->
                iniciarBusca(v.text.toString().trim())
            }
        }
        
        viewModel.resultadoBusca.observe(this, {
            binding.toolbar.subtitle = "Resultados: ${viewModel.resultCount}"
    
            (binding.recyclerView.adapter as RegistroAdapter).submitList(it)
    
            binding.textViewSemResultados.visibility =
                if (viewModel.hasAnyResult()) View.VISIBLE else View.GONE
        })
    }
    
    private fun configureAdapter() {
        val adapter = RegistroAdapter()
        adapter.setClickListener(AdapterClickListener(
            onClick = {
                DetalhesRegistroDialog(this, it).apply{
                    setOnActionListener { action, registro ->
                        when (action) {
                            DetalhesRegistroDialog.ACTION_EDITAR -> abrirBottomSheetCriarRegistro(registro, this)
                            DetalhesRegistroDialog.ACTION_EXCLUIR -> confirmarExcluirRegistro(registro, this)
                        }
                    }
                    show()
                }
            }, onLongClick = { confirmarExcluirRegistro(it) })
        )
    
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }
    
    private fun abrirBottomSheetCriarRegistro(registro: Registro, dialog: DetalhesRegistroDialog? = null) {
        CriarRegistroBottomSheet(registro, onSave = { bottomSheet, it ->
            viewModel.salvar(it) {
                Toast.makeText(this, "Salvo!", Toast.LENGTH_SHORT).show()
                dialog?.bindData(it)
            }
            bottomSheet.dismiss()
        })
    }
    
    private fun confirmarExcluirRegistro(registro: Registro, dialog: DetalhesRegistroDialog? = null) {
        var msg = "Nome: ${registro.descricao}"
        msg += "\nValor: ${Utils.formatCurrency(registro.valor)}"
    
        AlertDialog.Builder(this).setTitle("Excluir registro?").setMessage(msg)
            .setPositiveButton("Excluir") { _, _ ->
                viewModel.excluir(registro) {
                    Toast.makeText(this, "Removido!", Toast.LENGTH_SHORT).show()
                    dialog?.dismiss()
                }
            }.setNegativeButton("Cancelar", null).show()
    
    }
    
    private fun iniciarBusca(busca: String): Boolean {
        if (busca.isNotBlank()) {
            viewModel.setBusca(busca)
        } else {
            (binding.recyclerView.adapter as RegistroAdapter)
                .submitList(null)
            binding.textViewSemResultados.visibility = View.VISIBLE
            binding.toolbar.subtitle = "Resultados: 0"
        }
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onContextItemSelected(item)
    }
    
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
}