package br.com.myself.ui.adapter

import android.view.Menu
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import br.com.myself.R
import br.com.myself.context.EntradaContext
import br.com.myself.model.dao.EntradaDAO
import br.com.myself.model.entity.Entrada
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.formattedDate
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.layout_section_header_entradas.view.*
import kotlinx.android.synthetic.main.layout_section_item_entradas.view.*
import java.util.ArrayList

class EntradasAdapterSection(
    val adapter: SectionedRecyclerViewAdapter,
    val entradas: ArrayList<Entrada>,
    val referenciaAnoMes: String
) : Section(SectionParameters.builder().itemResourceId(R.layout.layout_section_item_entradas)
    .headerResourceId(R.layout.layout_section_header_entradas).build()) {
    
    
    
    private var expanded: Boolean = true
    
    override fun getContentItemsTotal(): Int {
        return if (expanded) entradas.size else 0
    }
    
    override fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder = HeaderViewHolder(view)
    
    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
        (holder as HeaderViewHolder).bindView()
    }
    
    override fun getItemViewHolder(view: View): RecyclerView.ViewHolder = ItemViewHolder(view)
    
    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bindView(entradas[position])
    }
    
    private inner class HeaderViewHolder(headerView: View) : RecyclerView.ViewHolder(headerView) {
        fun bindView() {
            var total = 0.0
            for (entrada in entradas) total += entrada.valor
            
            val ano = referenciaAnoMes.substring(0, 4).toInt()
            val mes = referenciaAnoMes.substring(5).toInt()
            
            itemView.tv_section_header_entradas_title.text = Utils.getMesString(year = ano, month = mes)
            itemView.tv_section_header_entradas_total.text = Utils.formatCurrency(total)
            
            if (expanded) {
                itemView.iv_section_header_entradas_expand.setImageResource(R.drawable.ic_arrow_up)
            } else {
                itemView.iv_section_header_entradas_expand.setImageResource(R.drawable.ic_arrow_down)
            }
            
            itemView.setOnClickListener {
                expanded = !expanded
                adapter.notifyDataSetChanged()
            }
        }
    }
    
    private class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(entrada: Entrada) {
            itemView.tv_item_entradas_valor.text = Utils.formatCurrency(entrada.valor)
            itemView.tv_item_entradas_descricao.text = entrada.descricao
            itemView.tv_item_entradas_data.text = entrada.data?.formattedDate()
            
            val popupMenu = PopupMenu(itemView.context, itemView).also { inflateMenu(it, entrada) }
            
            itemView.setOnLongClickListener {
                popupMenu.show()
                true
            }
        }
    
        private fun inflateMenu(popupMenu: PopupMenu, entrada: Entrada): PopupMenu {
            popupMenu.menu.add(Menu.NONE, 0, Menu.NONE, "Editar")
                .setOnMenuItemClickListener {
                    Trigger.launch(Events.EditarEntrada(entrada))
                    
                    true
                }
    
            popupMenu.menu.add(Menu.NONE, 1, Menu.NONE, "Excluir")
                .setOnMenuItemClickListener {
                    var mensagem = "Deseja realmente excluir a entrada?"
                    mensagem += "\n\nFonte: ${entrada.descricao}"
                    mensagem += "\nValor: ${Utils.formatCurrency(entrada.valor)}"
        
                    AlertDialog.Builder(itemView.context)
                        .setTitle("Excluir")
                        .setMessage(mensagem)
                        .setPositiveButton("Excluir") { _, _ ->
    
                            EntradaDAO(itemView.context).deletar(entrada)
                            Trigger.launch(Events.Toast("Excluído!"), Events.UpdateEntradas)
                
                        }.setNegativeButton("Cancelar", null).show()
                    true
                }
            
            return popupMenu
        }
    
    }
    
}
