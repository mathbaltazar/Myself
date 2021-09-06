package com.baltazarstudio.myself.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.myself.R
import com.baltazarstudio.myself.model.Registro
import com.baltazarstudio.myself.ui.registros.DetalhesRegistroDialog
import com.baltazarstudio.myself.util.Utils
import com.baltazarstudio.myself.util.Utils.Companion.formattedDate
import kotlinx.android.synthetic.main.layout_section_item_registro.view.*

class RegistrosDaDespesaAdapter(context: Context, private val registros: ArrayList<Registro>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    private val layoutInflater = LayoutInflater.from(context)
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            layoutInflater.inflate(R.layout.layout_section_item_registro, parent, false))
    }
    
    override fun getItemCount(): Int {
        return registros.size
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bindView(position)
    }
    
    private inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        fun bindView(position: Int) {
            val movimento = registros[position]
            
            itemView.tv_registro_descricao.text = movimento.descricao
            itemView.tv_registro_valor.text = Utils.formatCurrency(movimento.valor)
            itemView.tv_registro_data.text = movimento.data!!.formattedDate()
            
            itemView.setOnClickListener {
                val dialog = DetalhesRegistroDialog(itemView.context, movimento)
                dialog.setOnEditedMovimento {
                    registros[position] = it
                    notifyItemChanged(position)
                }
                dialog.show()
            }
        }
    }
    
}
