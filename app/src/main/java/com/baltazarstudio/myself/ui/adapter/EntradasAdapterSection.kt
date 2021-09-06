package com.baltazarstudio.myself.ui.adapter

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.myself.R
import com.baltazarstudio.myself.context.EntradaContext
import com.baltazarstudio.myself.model.Entrada
import com.baltazarstudio.myself.observer.Events
import com.baltazarstudio.myself.observer.Trigger
import com.baltazarstudio.myself.ui.entradas.CriarEntradaDialog
import com.baltazarstudio.myself.util.Utils
import com.baltazarstudio.myself.util.Utils.Companion.formattedDate
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.section_header_entradas.view.*
import kotlinx.android.synthetic.main.section_item_entradas.view.*

class EntradasAdapterSection(
    private val adapter: SectionedRecyclerViewAdapter,
    private val entradas: List<Entrada>,
    private val mes: Int,
    private val ano: Int
) : Section(SectionParameters.builder().itemResourceId(R.layout.section_item_entradas)
    .headerResourceId(R.layout.section_header_entradas).build()) {
    
    private var expanded: Boolean = false
    
    override fun getContentItemsTotal(): Int {
        return if (expanded) entradas.size else 0
    }
    
    override fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder {
        return HeaderViewHolder(view)
    }
    
    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
        (holder as HeaderViewHolder).bindView()
    }
    
    override fun getItemViewHolder(view: View): RecyclerView.ViewHolder {
        return ItemViewHolder(view)
    }
    
    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bindView(position)
    }
    
    private inner class HeaderViewHolder(headerView: View) : RecyclerView.ViewHolder(headerView) {
        fun bindView() {
            var total = 0.0
            for (entrada in entradas) total += entrada.valor
            
            itemView.tv_section_header_entradas_title.text = Utils.getMesString(mes, ano)
            itemView.tv_section_header_entradas_total.text = Utils.formatCurrency(total)
            
            if (expanded) {
                itemView.iv_section_header_entradas_expand.setImageResource(R.drawable.ic_arrow_up)
                itemView.tv_section_header_entradas_total.visibility = View.VISIBLE
            } else {
                itemView.iv_section_header_entradas_expand.setImageResource(R.drawable.ic_arrow_down)
                itemView.tv_section_header_entradas_total.visibility = View.GONE
            }
            
            itemView.setOnClickListener {
                expanded = !expanded
                adapter.notifyDataSetChanged()
            }
        }
    }
    
    private inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        
        fun bindView(position: Int) {
            val entrada = entradas[position]
            
            itemView.setOnClickListener {
                val dialog = CriarEntradaDialog(itemView.context)
                dialog.edit(entrada)
                dialog.show()
            }
            
            itemView.setOnLongClickListener {
                AlertDialog.Builder(itemView.context).setTitle("Excluir")
                    .setMessage("Deseja realmente excluir a entrada?")
                    .setPositiveButton("Excluir") { _, _ ->
                        
                        EntradaContext.getDAO(itemView.context).deletar(entrada)
                        Trigger.launch(Events.Toast("Excluído!"), Events.UpdateEntradas())
                        
                    }.setNegativeButton("Cancelar", null).show()
                true
            }
            
            itemView.tv_item_entradas_valor.text = Utils.formatCurrency(entrada.valor)
            itemView.tv_item_entradas_descricao.text = entrada.descricao
            itemView.tv_item_entradas_data.text = entrada.data?.formattedDate()
            
        }
    
    }
    
}
