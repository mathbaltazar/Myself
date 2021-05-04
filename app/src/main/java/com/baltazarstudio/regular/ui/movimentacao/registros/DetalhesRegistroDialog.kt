package com.baltazarstudio.regular.ui.movimentacao.registros

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.DespesaContext
import com.baltazarstudio.regular.context.MovimentoContext
import com.baltazarstudio.regular.model.Movimento
import com.baltazarstudio.regular.observer.Trigger
import com.baltazarstudio.regular.observer.Events
import com.baltazarstudio.regular.ui.movimentacao.despesa.SelecionarDespesaDialog
import com.baltazarstudio.regular.util.Utils
import com.baltazarstudio.regular.util.Utils.Companion.formattedDate
import kotlinx.android.synthetic.main.dialog_detalhes_movimento.*

class DetalhesRegistroDialog(
    context: Context, private var movimento: Movimento
) : Dialog(context) {
    
    private var onEditedListener: (Movimento) -> Unit = {}
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
            dialog.edit(movimento) { movimento ->
                this.movimento = movimento
                bindData()
                onEditedListener(movimento)
            }
            dialog.show()
        }
        
        button_detalhes_movimento_excluir.setOnClickListener {
            AlertDialog.Builder(context).setTitle("Excluir")
                .setMessage("Deseja realmente excluir este registro?")
                .setPositiveButton("Excluir") { _, _ ->
                    MovimentoContext.getDAO(context).excluir(movimento)
                    
                    Trigger.launch(Events.Toast("Removido!"))
                    Trigger.launch(Events.UpdateRegistros())
                    Trigger.launch(Events.UpdateDespesas())
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
                            movimento.referenciaDespesa = null
                            MovimentoContext.getDAO(context).alterar(movimento)
                            Trigger.launch(Events.Snack("Despesa desvinculada!"))
                            Trigger.launch(Events.UpdateDespesas())
                            Trigger.launch(Events.UpdateRegistros())
                            onEditedListener(movimento)
                            temDespesaAgregada = false
                            bindData()
                        
                        } else {
                        
                            val dialog = SelecionarDespesaDialog(context) { codigoDespesa ->
                                movimento.referenciaDespesa = codigoDespesa
                                MovimentoContext.getDAO(context).alterar(movimento)
                                Trigger.launch(Events.Snack("Despesa vinculada!"))
                                Trigger.launch(Events.UpdateDespesas())
                                Trigger.launch(Events.UpdateRegistros())
                                onEditedListener(movimento)
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
        tv_detalhes_movimento_descricao.text = movimento.descricao
        tv_detalhes_movimento_data.text = movimento.data?.formattedDate()
        tv_detalhes_movimento_valor.text = Utils.formatCurrency(movimento.valor)
        
        if (temDespesaAgregada) {
            tv_detalhes_movimento_referencia_despesa.visibility = View.VISIBLE
        } else {
            tv_detalhes_movimento_referencia_despesa.visibility = View.GONE
        }
    }
    
    private fun verificarSeTemDespesaAgregada() {
        temDespesaAgregada = if (movimento.referenciaDespesa != null && movimento.referenciaDespesa != 0) {
            val despesa = DespesaContext.getDAO(context).getDespesaPorCodigo(movimento.referenciaDespesa!!)
            despesa != null
        } else false
    }
    
    fun setOnEditedMovimento(onEditedListener: (Movimento) -> Unit) {
        this.onEditedListener = onEditedListener
    }
}
