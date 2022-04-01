package br.com.myself.ui.financas.registros

import android.os.Bundle
import android.os.PersistableBundle
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

class PesquisarRegistrosDialog : AppCompatActivity() {
    
    private val registroRepository = RegistroRepository(applicationContext)
    
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_pesquisar_registros)
    
    
        button_dialog_pesquisar_registros_fechar.setOnClickListener {
            finish()
        }
    
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
            val busca = et_dialog_pesquisar_registros_busca.text.toString()
        
            if (!busca.isBlank()) {
                Async.doInBackground({ registroRepository.pesquisarRegistros(busca) }, { resultadoBusca ->
                    til_dialog_pesquisar_registros_busca.helperText =
                        "Resultados: ${resultadoBusca.size}"
                
                    adapter.submitList(resultadoBusca)
                
                    tv_dialog_pesquisar_registros_sem_resultados.visibility =
                        if (resultadoBusca.isEmpty()) View.VISIBLE else View.GONE
                })
            }
        }
    
    }
}