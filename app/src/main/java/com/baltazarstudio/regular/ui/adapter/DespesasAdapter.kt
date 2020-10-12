package com.baltazarstudio.regular.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.DespesaContext
import com.baltazarstudio.regular.context.MovimentoContext
import com.baltazarstudio.regular.model.Despesa
import com.baltazarstudio.regular.model.Movimento
import com.baltazarstudio.regular.observer.Trigger
import com.baltazarstudio.regular.observer.TriggerEvent
import com.baltazarstudio.regular.ui.registros.despesa.RegistrarDespesaDialog
import com.baltazarstudio.regular.ui.registros.despesa.MovimentosDespesasDialog
import com.baltazarstudio.regular.util.Utils
import com.baltazarstudio.regular.util.Utils.Companion.formattedDate
import kotlinx.android.synthetic.main.layout_section_item_despesa.view.*
import java.util.*

class DespesasAdapter(context: Context, private val despesas: ArrayList<Despesa>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    private val layoutInflater = LayoutInflater.from(context)
    private var expandedItemPosition: Int = -1
    
    override fun getItemCount(): Int {
        return despesas.size
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
            val despesa = despesas[position]
            
            itemView.setOnClickListener {
                val lastExpanded = expandedItemPosition
                expandedItemPosition = position
                
                if (lastExpanded == -1) {
                    notifyItemChanged(expandedItemPosition)
                } else if (lastExpanded == position) {
                    expandedItemPosition = -1
                    notifyItemChanged(lastExpanded)
                } else {
                    notifyItemChanged(lastExpanded)
                    notifyItemChanged(expandedItemPosition)
                }
            }
            
            
            if (expandedItemPosition == position) {
                itemView.divider_section_item_despesas.visibility = View.VISIBLE
                itemView.ll_section_item_despesas_acoes.visibility = View.VISIBLE
                itemView.iv_section_item_despesas_expand.setImageResource(R.drawable.ic_arrow_up)
            } else {
                itemView.divider_section_item_despesas.visibility = View.GONE
                itemView.ll_section_item_despesas_acoes.visibility = View.GONE
                itemView.iv_section_item_despesas_expand.setImageResource(R.drawable.ic_arrow_down)
            }
            
            // NOME
            itemView.tv_section_item_despesas_nome.text = despesa.nome
            
            // ULTIMO REGISTRO
            val ultimoRegistro = obterUltimoRegistro(despesa.codigo!!)
            if (ultimoRegistro != null) {
                itemView.tv_section_item_despesas_ultimo_registro.text = "Último registro: ${ultimoRegistro.formattedDate()}"
            } else {
                itemView.tv_section_item_despesas_ultimo_registro.text = "Não há registros."
            }
            
            // VALOR
            itemView.tv_section_item_despesas_valor.text = Utils.formatCurrency(despesa.valor)
            
            // REGISTRAR
            itemView.button_section_item_despesas_registrar.setOnClickListener {
                val dialog = RegistrarDespesaDialog(itemView.context, despesa)
                dialog.show()
            }
            
            // TODAS DESPESAS
            itemView.button_section_item_despesas_todos.isEnabled = ultimoRegistro != null
            itemView.button_section_item_despesas_todos.setOnClickListener {
                val registrosDaDespesa = MovimentoContext.getDAO(itemView.context).getMovimentosPorTipo(Movimento.DESPESA)
                    .filter { it.referenciaDespesa == despesa.codigo }
    
                val dialog = MovimentosDespesasDialog(itemView.context, registrosDaDespesa)
                dialog.show()
            }
            
            // EXCLUIR DESPESA
            itemView.setOnLongClickListener {
                AlertDialog.Builder(itemView.context).setTitle("Excluir")
                    .setMessage("Deseja realmente deletar esta despesa?")
                    .setPositiveButton("Excluir") { _, _ ->
                        DespesaContext.getDAO(itemView.context).deletar(despesa)
                        Toast.makeText(itemView.context, "Removido!", Toast.LENGTH_SHORT).show()
                        despesas.remove(despesa)
                        /*notifyItemRemoved(position)
                        reassignExpadedItem(position)*/
                        Trigger.launch(TriggerEvent.UpdateTelaDespesa())
                    }.setNegativeButton("Cancelar", null)
                    .show()
                true
            }
        }
    
        private fun obterUltimoRegistro(codigo: Int): Long? {
            val registrosDespesas = MovimentoContext.getDAO(itemView.context).getMovimentosPorTipo(Movimento.DESPESA)
                .filter { it.referenciaDespesa == codigo }
            
            if (registrosDespesas.isNullOrEmpty())
                return null
            
            return registrosDespesas.sortedByDescending { it.data }.first().data
        }
    
        /*private fun reassignExpadedItem(excludedPosition: Int) {
            if (excludedPosition == expandedItemPosition)
                expandedItemPosition = -1
            else if (excludedPosition < expandedItemPosition)
                expandedItemPosition--
        }*/
    
    }
}
