package br.com.myself.ui.registros

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import com.com.myself.R
import br.com.myself.context.DespesaContext
import br.com.myself.context.RegistroContext
import br.com.myself.model.Registro
import br.com.myself.observer.Trigger
import br.com.myself.observer.Events
import br.com.myself.ui.despesa.SelecionarDespesaDialog
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.formattedDate
import kotlinx.android.synthetic.main.dialog_detalhes_movimento.*

class DetalhesRegistroDialog(
    context: Context, private var registro: Registro
) : Dialog(context) {
    
    private var onEditedListener: (Registro) -> Unit = {}
    private var temDespesaAgregada: Boolean = false
    
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(R.layout.dialog_detalhes_movimento)
        
        setUpView()
        show()
    }
    
    private fun setUpView() {
        
        verificarSeTemDespesaAgregada()
        bindData()
        
        button_detalhes_movimento_alterar.setOnClickListener {
            val dialog = CriarRegistroDialog(context)
            dialog.edit(registro) { movimento ->
                this.registro = movimento
                bindData()
                onEditedListener(movimento)
            }
            dialog.show()
        }
        
        button_detalhes_movimento_excluir.setOnClickListener {
            AlertDialog.Builder(context).setTitle("Excluir")
                .setMessage("Deseja realmente excluir este registro?")
                .setPositiveButton("Excluir") { _, _ ->
                    RegistroContext.getDAO(context).excluir(registro)
                    
                    Trigger.launch(
                        Events.Toast("Removido!"),
                        Events.UpdateRegistros(),
                        Events.UpdateDespesas()
                    )
                    
                    cancel()
                }.setNegativeButton("Cancelar", null).show()
        }
        
        button_detalhes_movimento_opcoes.setOnClickListener {
            val popup = PopupMenu(context, it, Gravity.END)
            
            val titulo = if (temDespesaAgregada) "Desvincular da Despesa" else "Vincular a Despesa"
            
            popup.menu.add(Menu.NONE, 0, Menu.NONE, titulo)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    0 -> {
                        
                        if (temDespesaAgregada) {
                            registro.referenciaDespesa = null
                            RegistroContext.getDAO(context).alterar(registro)
                            Trigger.launch(
                                Events.Snack("Despesa desvinculada!"),
                                Events.UpdateDespesas(),
                                Events.UpdateRegistros()
                            )
                            onEditedListener(registro)
                            temDespesaAgregada = false
                            bindData()
                            
                        } else {
                            
                            val dialog =
                                SelecionarDespesaDialog(
                                    context
                                ) { codigoDespesa ->
                                    registro.referenciaDespesa = codigoDespesa
                                    RegistroContext.getDAO(
                                        context
                                    ).alterar(registro)
                                    Trigger.launch(
                                        Events.Snack("Despesa vinculada!"),
                                        Events.UpdateDespesas(),
                                        Events.UpdateRegistros()
                                    )
                                    onEditedListener(registro)
                                    temDespesaAgregada = true
                                    bindData()
                                }
                            dialog.show()
                            
                        }
                        
                    }
                }
                true
            }
            popup.show()
        }
    }
    
    private fun bindData() {
        tv_detalhes_movimento_descricao.text = registro.descricao
        tv_detalhes_movimento_data.text = registro.data?.formattedDate()
        tv_detalhes_movimento_valor.text = Utils.formatCurrency(registro.valor)
        
        if (temDespesaAgregada) {
            tv_detalhes_movimento_referencia_despesa.visibility = View.VISIBLE
        } else {
            tv_detalhes_movimento_referencia_despesa.visibility = View.GONE
        }
    }
    
    private fun verificarSeTemDespesaAgregada() {
        temDespesaAgregada =
            if (registro.referenciaDespesa != null && registro.referenciaDespesa != 0) {
                val despesa = DespesaContext.getDAO(context)
                    .getDespesaPorCodigo(registro.referenciaDespesa!!)
                despesa != null
            } else false
    }
    
    fun setOnEditedMovimento(onEditedListener: (Registro) -> Unit) {
        this.onEditedListener = onEditedListener
    }
}
