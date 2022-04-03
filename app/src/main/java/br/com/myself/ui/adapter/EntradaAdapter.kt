package br.com.myself.ui.adapter

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.myself.R
import br.com.myself.model.dao.EntradaDAO
import br.com.myself.model.entity.Entrada
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.formattedDate
import kotlinx.android.synthetic.main.layout_adapter_entrada.view.*

class EntradaAdapter : ListAdapter<Entrada, RecyclerView.ViewHolder>(COMPARATOR) {
    
    private object COMPARATOR : DiffUtil.ItemCallback<Entrada>() {
        override fun areItemsTheSame(oldItem: Entrada, newItem: Entrada): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Entrada, newItem: Entrada): Boolean = oldItem == newItem
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EntradaViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_adapter_entrada, parent, false)
        )
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as EntradaViewHolder).bindView(getItem(position))
    }
    
    private class EntradaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(entrada: Entrada) {
            itemView.tv_item_entradas_valor.text = Utils.formatCurrency(entrada.valor)
            itemView.tv_item_entradas_descricao.text = entrada.descricao
            itemView.tv_item_entradas_data.text = entrada.data.formattedDate()
            
            val popupMenu = PopupMenu(itemView.context, itemView).also { inflateMenu(it, entrada) }
            
            itemView.setOnLongClickListener {
                popupMenu.show()
                true
            }
        }
        
        private fun inflateMenu(popupMenu: PopupMenu, entrada: Entrada): PopupMenu {
            popupMenu.menu.add(Menu.NONE, 0, Menu.NONE, "Editar").setOnMenuItemClickListener {
                Trigger.launch(Events.EditarEntrada(entrada))
                
                true
            }
            
            popupMenu.menu.add(Menu.NONE, 1, Menu.NONE, "Excluir").setOnMenuItemClickListener {
                var mensagem = "Deseja realmente excluir a entrada?"
                mensagem += "\n\nFonte: ${entrada.descricao}"
                mensagem += "\nValor: ${Utils.formatCurrency(entrada.valor)}"
                
                AlertDialog.Builder(itemView.context).setTitle("Excluir").setMessage(mensagem)
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
