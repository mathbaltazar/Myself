package com.baltazarstudio.regular.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.controller.GastosController
import com.baltazarstudio.regular.model.Gasto
import com.baltazarstudio.regular.util.Utils
import com.baltazarstudio.regular.util.Utils.Companion.formattedDate
import kotlinx.android.synthetic.main.layout_section_header_movimento.view.*
import kotlinx.android.synthetic.main.layout_section_item_movimento.view.*

class GastoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private val pairMesAno: Pair<Int, Int>?
    private val itens: List<Gasto>
    private val controller: GastosController?
    
    constructor(
        context: Context,
        pairMesAno: Pair<Int, Int>,
        itens: List<Gasto>,
        controller: GastosController
    ) : super() {
        this.pairMesAno = pairMesAno
        this.itens = itens
        this.controller = controller
        this.layoutInflater = LayoutInflater.from(context)
    }
    
    constructor(context: Context, itens: List<Gasto>) : super() {
        this.pairMesAno = null
        this.itens = itens
        this.controller = null
        this.layoutInflater = LayoutInflater.from(context)
    }
    
    companion object {
        private const val HEADER_VIEW_TYPE = 100
    }
    
    private val layoutInflater: LayoutInflater
    
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (pairMesAno != null && viewType == HEADER_VIEW_TYPE) return HeaderViewHolder(
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
        return if (pairMesAno != null) itens.size + 1 else itens.size
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) holder.bindHeader()
        else (holder as ItemViewHolder).bindView(position - if (pairMesAno != null) 1 else 0)
    }
    
    override fun getItemViewType(position: Int): Int {
        if (pairMesAno != null && position == 0) return HEADER_VIEW_TYPE
        return super.getItemViewType(position)
    }
    
    private inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        fun bindView(position: Int) {
            
            // TODO Elaborar view para despesas
            
            val gasto = itens[position]
            itemView.tv_movimento_descricao.text = gasto.descricao
            itemView.tv_movimento_valor.text = Utils.formatCurrency(gasto.valor)
            itemView.tv_movimento_data.text = gasto.data.formattedDate()
            
            if (controller != null) {
                itemView.setOnClickListener {
                    controller.editarGasto(gasto)
                }
                itemView.setOnLongClickListener {
                    AlertDialog.Builder(itemView.context).setTitle("Excluir")
                        .setMessage("Confirmar exclusão").setPositiveButton("Sim") { _, _ ->
                            controller.excluir(gasto)
                            Toast.makeText(itemView.context, "Removido!", Toast.LENGTH_SHORT).show()
                            controller.carregarGastos()
                        }.setNegativeButton("Não", null).show()
                    true
                }
            }
        }
    }
    
    private inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    
        fun bindHeader() {
            itemView.tv_header_movimento_title.text =
                    String.format("%s/%d", getMesString(pairMesAno!!.first), pairMesAno.second)
        
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
}
