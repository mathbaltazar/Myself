package com.baltazarstudio.regular.ui.adapter

import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.MovimentoContext
import com.baltazarstudio.regular.model.Movimento
import com.baltazarstudio.regular.observer.Trigger
import com.baltazarstudio.regular.observer.TriggerEvent
import com.baltazarstudio.regular.ui.registros.movimentos.RegistrarMovimentoDialog
import com.baltazarstudio.regular.util.Utils
import com.baltazarstudio.regular.util.Utils.Companion.formattedDate
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import kotlinx.android.synthetic.main.layout_section_header_movimento.view.*
import kotlinx.android.synthetic.main.layout_section_item_movimento.view.*

class MovimentosSectionAdapter(
    private val ano: Int,
    private val mes: Int,
    private val movimentos: List<Movimento>
) : Section(SectionParameters.builder()
    .headerResourceId(R.layout.layout_section_header_movimento)
    .itemResourceId(R.layout.layout_section_item_movimento)
    .build()) {
    
    override fun getContentItemsTotal(): Int {
        return movimentos.size
    }
    
    override fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder {
        return HeaderViewHolder(view)
    }
    
    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
        var total = 0.0
        for (movimento in movimentos) total += movimento.valor
        (holder as HeaderViewHolder).bindView(mes, ano, total)
    }
    
    override fun getItemViewHolder(view: View): RecyclerView.ViewHolder {
        return ItemViewHolder(view)
    }
    
    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as ItemViewHolder).bindView(movimentos[position])
    }
    
    private class HeaderViewHolder(headerView: View) : RecyclerView.ViewHolder(headerView) {
        fun bindView(mes: Int, ano: Int, total: Double) {
            itemView.tv_header_movimento_title.text = "${Utils.getMesString(mes)}/$ano"
            itemView.tv_header_movimento_total.text = Utils.formatCurrency(total)
        }
    }
    
    private class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(movimento: Movimento) {
            
            itemView.tv_movimento_descricao.text = movimento.descricao
            itemView.tv_movimento_valor.text = Utils.formatCurrency(movimento.valor)
            itemView.tv_movimento_data.text = movimento.data!!.formattedDate()
            
            itemView.setOnClickListener {
                val dialog = RegistrarMovimentoDialog(itemView.context)
                dialog.edit(movimento)
                dialog.show()
            }
            
            itemView.setOnLongClickListener {
                AlertDialog.Builder(itemView.context).setTitle("Excluir")
                    .setMessage("Deseja realmente excluir este registro?")
                    .setPositiveButton("Excluir") { _, _ ->
                        MovimentoContext.getDAO(itemView.context).excluir(movimento)
                        Toast.makeText(itemView.context, "Removido!", Toast.LENGTH_SHORT).show()
                        Trigger.launch(TriggerEvent.UpdateTelaMovimento())
                    }.setNegativeButton("Cancelar", null)
                    .show()
                true
            }
        }
    }
    
}
