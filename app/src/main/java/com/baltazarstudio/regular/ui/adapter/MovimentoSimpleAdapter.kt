package com.baltazarstudio.regular.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.model.Movimento
import com.baltazarstudio.regular.ui.registros.movimentos.RegistrarMovimentoDialog
import com.baltazarstudio.regular.util.Utils
import com.baltazarstudio.regular.util.Utils.Companion.formattedDate
import kotlinx.android.synthetic.main.layout_section_item_movimento.view.*

class MovimentoSimpleAdapter(context: Context, private val movimentos: List<Movimento>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    private val layoutInflater = LayoutInflater.from(context)
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            layoutInflater.inflate(R.layout.layout_section_item_movimento, parent, false))
    }
    
    override fun getItemCount(): Int {
        return movimentos.size
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bindView(position)
    }
    
    private inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        fun bindView(position: Int) {
            val movimento = movimentos[position]
            
            itemView.tv_movimento_descricao.text = movimento.descricao
            itemView.tv_movimento_valor.text = Utils.formatCurrency(movimento.valor)
            itemView.tv_movimento_data.text = movimento.data!!.formattedDate()
            
            itemView.setOnClickListener {
                val dialog = RegistrarMovimentoDialog(itemView.context)
                dialog.edit(movimento)
                dialog.show()
            }
        }
    }
    
}
