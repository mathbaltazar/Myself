package com.baltazarstudio.regular.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.EntradaContext
import com.baltazarstudio.regular.model.Entrada
import com.baltazarstudio.regular.util.Utils
import com.baltazarstudio.regular.util.Utils.Companion.formattedDate
import kotlinx.android.synthetic.main.list_item_entradas.view.*
import org.jetbrains.anko.toast

class EntradasAdapter(context: Context, private val entradas: ArrayList<Entrada>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var entradaImpl: EntradaInterface? = null
    
    init {
        entradas.sortByDescending { it.data }
    }
    
    override fun getItemCount(): Int {
        return entradas.size
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(layoutInflater.inflate(R.layout.list_item_entradas, parent, false))
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bindView(position)
    }
    
    fun addEntrada(entrada: Entrada): Int {
        entradas.add(entrada)
        entradas.sortBy { it.data }
        
        val position = entradas.indexOf(entrada)
        
        notifyItemInserted(position)
        entradaImpl?.onAdded(entrada)
        return position
    }
    
    private inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        
        fun bindView(position: Int) {
            val entrada = entradas[position]
            
            itemView.setOnLongClickListener {
                AlertDialog.Builder(itemView.context).setTitle("Atenção")
                    .setMessage("Confirma a exclusão da entrada?")
                    .setPositiveButton("Sim") { _, _ ->
                        EntradaContext.getDAO(itemView.context).deletar(entrada)
                        itemView.context.toast("Excluído!")
                        entradas.remove(entrada)
                        notifyItemRemoved(adapterPosition)
                        entradaImpl?.onExcluded(entrada)
                    }.setNegativeButton("Não", null).show()
                true
            }
            
            itemView.tv_item_entradas_valor.text = Utils.formatCurrency(entrada.valor)
            itemView.tv_item_entradas_descricao.text = entrada.descricao
            itemView.tv_item_entradas_data.text = entrada.data?.formattedDate()
            
        }
    
    }
    
    interface EntradaInterface {
        fun onAdded(entrada: Entrada)
        fun onExcluded(entrada: Entrada)
    }
    
    fun setEntradaInterface(mInterface: EntradaInterface) {
        entradaImpl = mInterface
    }
}
