package com.baltazarstudio.regular.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.model.Movimento
import com.baltazarstudio.regular.util.Utils
import com.baltazarstudio.regular.util.Utils.Companion.formattedDate
import kotlinx.android.synthetic.main.layout_section_header_movimento.view.*
import kotlinx.android.synthetic.main.layout_section_item_movimento.view.*

class MovimentoAdapter(
    context: Context,
    private val pairMesAno: Pair<Int, Int>,
    private val itens: List<Movimento>,
    private val listener: ItemActionListener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    private val layoutInflater = LayoutInflater.from(context)
    private val HEADER_VIEW_TYPE = 100
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == HEADER_VIEW_TYPE) return HeaderViewHolder(
            layoutInflater.inflate(
                R.layout.layout_section_header_movimento, parent, false
            )
        )
        return ItemViewHolder(
            layoutInflater.inflate(
                R.layout.layout_section_item_movimento, parent, false
            )
        )
    }
    
    override fun getItemCount(): Int {
        return itens.size + 1
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) holder.bindHeader()
        else (holder as ItemViewHolder).bindView(position - 1)
    }
    
    override fun getItemViewType(position: Int): Int {
        if (position == 0) return HEADER_VIEW_TYPE
        return super.getItemViewType(position)
    }
    
    private inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        fun bindView(position: Int) {
            val movimento = itens[position]
            itemView.tv_movimento_descricao.text = movimento.descricao
            itemView.tv_movimento_valor.text = Utils.formatCurrency(movimento.valor)
            itemView.tv_movimento_data.text = movimento.data.formattedDate()
            
            itemView.setOnClickListener {
                listener?.click(movimento, this@MovimentoAdapter, position)
            }
            itemView.setOnLongClickListener {
                listener?.longClick(movimento, this@MovimentoAdapter, position)
                true
            }
        }
    }
    
    private inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    
        fun bindHeader() {
        
            itemView.tv_header_movimento_title.text =
                    String.format("%s/%d", getMesString(pairMesAno.first), pairMesAno.second)
        
            var total = 0.0
            for (movimento in itens) {
                total += movimento.valor
            }
            itemView.tv_header_movimento_total.text = Utils.formatCurrency(total)
        }
    
        private fun getMesString(mes: Int): String {
            return when (mes) {
                1 -> "JANEIRO"
                2 -> "FEVEREIRO"
                3 -> "MARÇO"
                4 -> "ABRIL"
                5 -> "MAIO"
                6 -> "JUNHO"
                7 -> "JULHO"
                8 -> "AGOSTO"
                9 -> "SETEMBRO"
                10 -> "OUTUBRO"
                11 -> "NOVEMBRO"
                12 -> "DEZEMBRO"
                else -> ""
            }
        }
        
    }
    
    interface ItemActionListener {
    
        fun click(item: Movimento, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, position: Int)
    
        fun longClick(item: Movimento, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, position: Int)
    }
}
