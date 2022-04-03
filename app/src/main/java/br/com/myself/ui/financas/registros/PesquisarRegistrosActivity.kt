package br.com.myself.ui.financas.registros

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.R
import br.com.myself.model.repository.RegistroRepository
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.ui.adapter.RegistroAdapter
import br.com.myself.util.AdapterClickListener
import br.com.myself.util.Async
import br.com.myself.util.Utils
import kotlinx.android.synthetic.main.activity_pesquisar_registros.*

class PesquisarRegistrosActivity : AppCompatActivity() {
    
    private val registroRepository by lazy { RegistroRepository(applicationContext) }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesquisar_registros)
        setSupportActionBar(toolbar_pesquisar_registro)
        
        val adapter = RegistroAdapter()
        adapter.setClickListener(AdapterClickListener(
            onClick = {
                DetalhesRegistroDialog(this, it).show()
            }, onLongClick = { registro ->
                var msg = "Nome: ${registro.descricao}"
                msg += "\nValor: ${Utils.formatCurrency(registro.valor)}"
        
                AlertDialog.Builder(this).setTitle("Excluir registro?").setMessage(msg)
                    .setPositiveButton("Excluir") { _, _ ->
                        Async.doInBackground({
                            registroRepository.excluirRegistro(registro)
                        }, {
                            Trigger.launch(Events.UpdateRegistros)
                        })
                    }.setNegativeButton("Cancelar", null).show()
            }))
    
        rv_dialog_pesquisar_registros_resultados_busca.layoutManager = LinearLayoutManager(this)
        rv_dialog_pesquisar_registros_resultados_busca.adapter = adapter
    
        til_dialog_pesquisar_registros_busca.setEndIconOnClickListener {
            buscar(til_dialog_pesquisar_registros_busca.editText?.text.toString())
        }
        
        til_dialog_pesquisar_registros_busca.editText?.setOnEditorActionListener { v, _, _ ->
            buscar(v.text.toString())
        }
    }
    
    private fun buscar(busca: String): Boolean {
        if (!busca.isBlank()) {
            Async.doInBackground({ registroRepository.pesquisarRegistros(busca) },
                { resultadoBusca ->
                    toolbar_pesquisar_registro.subtitle = "Resultados: ${resultadoBusca.size}"
            
                    (rv_dialog_pesquisar_registros_resultados_busca.adapter as RegistroAdapter)
                        .submitList(resultadoBusca)
            
                    tv_dialog_pesquisar_registros_sem_resultados.visibility =
                        if (resultadoBusca.isEmpty()) View.VISIBLE else View.GONE
                })
        } else {
            (rv_dialog_pesquisar_registros_resultados_busca.adapter as RegistroAdapter)
                .submitList(null)
            tv_dialog_pesquisar_registros_sem_resultados.visibility = View.VISIBLE
            toolbar_pesquisar_registro.subtitle = "Resultados: 0"
        }
        
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onContextItemSelected(item)
    }
    
}