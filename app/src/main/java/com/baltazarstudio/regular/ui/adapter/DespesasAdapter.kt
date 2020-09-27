package com.baltazarstudio.regular.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.controller.DespesasController
import com.baltazarstudio.regular.model.Despesa
import com.baltazarstudio.regular.ui.despesa.RegistrarDespesaDialog
import com.baltazarstudio.regular.util.Utils
import com.baltazarstudio.regular.util.Utils.Companion.formattedDate
import kotlinx.android.synthetic.main.layout_section_item_despesa.view.*
import java.util.*

class DespesasAdapter(
    context: Context,
    private val despesas: ArrayList<Despesa>,
    private val controller: DespesasController
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    private val layoutInflater = LayoutInflater.from(context)
    private var expandedItem: Int = -1
    
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
                val lastExpanded = expandedItem
                expandedItem = position
                
                if (lastExpanded == -1) {
                    notifyItemChanged(expandedItem)
                } else if (lastExpanded == position) {
                    expandedItem = -1
                    notifyItemChanged(lastExpanded)
                } else {
                    notifyItemChanged(lastExpanded)
                    notifyItemChanged(expandedItem)
                }
            }
            
            
            if (expandedItem == position) {
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
            val temRegistro = despesa.ultimoRegistro != 0L
            if (temRegistro) {
                itemView.tv_section_item_despesas_ultimo_registro.text = "Último registro: ${despesa.ultimoRegistro.formattedDate()}"
            } else {
                itemView.tv_section_item_despesas_ultimo_registro.text = "Não há registros."
            }
            
            // VALOR
            itemView.tv_section_item_despesas_valor.text = Utils.formatCurrency(despesa.valor)
            
            // REGISTRAR
            itemView.button_section_item_despesas_registrar.setOnClickListener {
                val dialog =
                    RegistrarDespesaDialog(
                        itemView.context,
                        despesa,
                        controller
                    )
                dialog.show()
            }
            
            // TODAS DESPESAS
            itemView.button_section_item_despesas_todos.isEnabled = temRegistro
            itemView.button_section_item_despesas_todos.setOnClickListener {
                controller.mostrarTodosRegistros(despesa.referencia!!)
            }
        }
        
    }
}
