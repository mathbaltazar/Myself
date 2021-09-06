package br.com.myself.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.com.myself.R
import br.com.myself.model.Despesa
import br.com.myself.util.Utils
import kotlinx.android.synthetic.main.list_item_selecionar_despesa.view.*

class SelecionarDespesaAdapter(
    context: Context,
    private val despesas: List<Despesa>,
    private val onItemSelected: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    private val inflater = LayoutInflater.from(context)
    
    override fun getItemCount(): Int {
        return despesas.size
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            inflater.inflate(R.layout.list_item_selecionar_despesa, parent, false)
        )
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val despesa = despesas[position]
        
        holder.itemView.tv_selecionar_despesa_descricao.text = despesa.nome
        holder.itemView.tv_selecionar_despesa_valor.text = Utils.formatCurrency(despesa.valor)
        
        holder.itemView.ll_list_item_selecionar_despesa.setOnClickListener {
            onItemSelected(despesa.codigo!!)
        }
    }
    
    private class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    
}
