package com.baltazarstudio.myself.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.myself.R
import com.baltazarstudio.myself.context.DespesaContext
import com.baltazarstudio.myself.context.RegistroContext
import com.baltazarstudio.myself.model.Despesa
import com.baltazarstudio.myself.model.enum.Boolean
import com.baltazarstudio.myself.observer.Events
import com.baltazarstudio.myself.observer.Trigger
import com.baltazarstudio.myself.util.Utils
import com.baltazarstudio.myself.util.Utils.Companion.formattedDate
import kotlinx.android.synthetic.main.layout_section_item_despesa.view.*

class DespesasAdapter(context: Context, private val listaDespesas: List<Despesa>, val onDespesaItemClickListener: () -> Unit)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    private val layoutInflater = LayoutInflater.from(context)
    
    override fun getItemCount(): Int {
        return listaDespesas.size
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            layoutInflater.inflate(R.layout.layout_section_item_despesa, parent, false)
        )
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bindView(position)
    }
    
    private inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(position: Int) {
            val despesa = listaDespesas[position]
            
            itemView.setOnClickListener {
                DespesaContext.despesaDetalhada = despesa
                
                onDespesaItemClickListener()
            }
            
            // NOME
            itemView.tv_section_item_despesas_nome.text = despesa.nome
            
            // ULTIMO REGISTRO
            val ultimoRegistro = RegistroContext.getDAO(itemView.context).getUltimoRegistro(despesa.codigo!!)
            if (ultimoRegistro == 0L) {
                itemView.tv_section_item_despesas_ultimo_registro.text = "Não há registros."
            } else {
                itemView.tv_section_item_despesas_ultimo_registro.text = "Último registro: ${ultimoRegistro.formattedDate()}"
            }
            
            // DIA VENCIMENTO
            if (despesa.diaVencimento != 0) {
                itemView.ll_section_item_despesas_vencimento.visibility = View.VISIBLE
                itemView.tv_section_item_despesas_dia_vencimento.text = despesa.diaVencimento.toString()
            } else {
                itemView.ll_section_item_despesas_vencimento.visibility = View.GONE
            }
    
            // VALOR
            itemView.tv_section_item_despesas_valor.text = Utils.formatCurrency(despesa.valor)
            
            // OPÇOES
            itemView.iv_section_item_despesas_opcoes.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, it)
                popupMenu.menu.add(Menu.NONE, 0, Menu.NONE, "Excluir")
                popupMenu.menu.add(Menu.NONE, 1, Menu.NONE, "Arquivar")
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        0 -> excluirDepesa(despesa)
                        1 -> arquivar(despesa)
                    }
                    false
                }
                popupMenu.show()
            }
            
            // ESCONDER ÚLTIMO DIVIDER
            if (despesa == listaDespesas.last()) {
                itemView.divider_section_item_despesas.visibility = View.GONE
            }
        }
    
        private fun arquivar(despesa: Despesa) {
            despesa.arquivado = Boolean.TRUE
            
            DespesaContext.getDAO(itemView.context).alterar(despesa)
            Trigger.launch(Events.Snack("Despesa arquivada"), Events.UpdateDespesas())
        }
    
        private fun excluirDepesa(despesa: Despesa) {
            AlertDialog.Builder(itemView.context).setTitle("Excluir")
                .setMessage("Deseja realmente deletar esta despesa?")
                .setPositiveButton("Excluir") { _, _ ->
                    
                    DespesaContext.getDAO(itemView.context).deletar(despesa)
                    Trigger.launch(Events.Toast("Removido!"), Events.UpdateDespesas())
                    
                }.setNegativeButton("Cancelar", null)
                .show()
        }
    
    }
}
