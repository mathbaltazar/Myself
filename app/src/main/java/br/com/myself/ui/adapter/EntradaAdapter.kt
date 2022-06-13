package br.com.myself.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.myself.R
import br.com.myself.databinding.AdapterEntradaItemBinding
import br.com.myself.databinding.AdapterEntradaSeparatorBinding
import br.com.myself.ui.utils.UIModel
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.formattedDate

const val ITEM_VIEW_TYPE = 0
const val SEPARATOR_VIEW_TYPE = 1

class EntradaAdapter :
    PagingDataAdapter<UIModel, RecyclerView.ViewHolder>(COMPARATOR) {
    
    private var onItemClick: ((Long) -> Unit)? = null
    
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UIModel.SeparatorEntrada -> SEPARATOR_VIEW_TYPE
            is UIModel.UIEntrada -> ITEM_VIEW_TYPE
            else -> super.getItemViewType(position)
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SEPARATOR_VIEW_TYPE -> SeparatorViewHolder.create(parent)
            ITEM_VIEW_TYPE -> ItemViewHolder.create(parent)
            else -> super.createViewHolder(parent, viewType)
        }
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val current = getItem(position)) {
            is UIModel.SeparatorEntrada -> bindSeparator(holder.itemView, current)
            is UIModel.UIEntrada -> bindItem(holder.itemView, current)
        }
    }
    
    
    private fun bindSeparator(itemView: View, separatorModel: UIModel.SeparatorEntrada) {
        val binding = AdapterEntradaSeparatorBinding.bind(itemView)
        binding.textviewMes.text = Utils.MESES_STRING[separatorModel.mes]
        
        itemView.setOnClickListener { /* TODO SEPARATOR INTERACTION ?? */ }
    }
    
    private fun bindItem(itemView: View, itemModel: UIModel.UIEntrada) {
        val binding = AdapterEntradaItemBinding.bind(itemView)
        
        with(itemModel) {
            binding.textviewValor.text = Utils.formatCurrency(entrada.valor)
            binding.textviewDescricao.text = entrada.descricao
            binding.textviewData.text = entrada.data.formattedDate()
            
            binding.root.setOnClickListener { onItemClick?.invoke(entrada.id!!) }
        }
    }
    
    fun setInteractionListener(
        onItemClick: ((Long) -> Unit)? = null
    ) {
        this.onItemClick = onItemClick
    }
    
    private class SeparatorViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        companion object {
            fun create(parent: ViewGroup) = SeparatorViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_entrada_separator, parent, false))
        }
    }
    private class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        companion object {
            fun create(parent: ViewGroup) = SeparatorViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_entrada_item, parent, false))
        }
    }
    
    companion object {
        private object COMPARATOR : DiffUtil.ItemCallback<UIModel>()  {
            override fun areItemsTheSame(oldItem: UIModel, newItem: UIModel): Boolean {
                return (oldItem is UIModel.UIEntrada && newItem is UIModel.UIEntrada && oldItem.entrada.id == oldItem.entrada.id)
                        || (oldItem is UIModel.SeparatorEntrada && newItem is UIModel.SeparatorEntrada && oldItem.mes == newItem.mes)
            }
            override fun areContentsTheSame(oldItem: UIModel, newItem: UIModel): Boolean {
                return oldItem == newItem
            }
        }
    }
    
}
