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
import br.com.myself.domain.entity.Entrada
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.formattedDate

const val ITEM_VIEW_TYPE = 0
const val SEPARATOR_VIEW_TYPE = 1

class EntradaAdapter :
    PagingDataAdapter<EntradaAdapter.UIModel, RecyclerView.ViewHolder>(UIMODEL_COMPARATOR) {
    
    private var onSeparatorClick: (() -> Unit)? = null
    private var onItemLongClick: ((View, Entrada) -> Unit)? = null
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SEPARATOR_VIEW_TYPE -> SeparatorViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_entrada_separator, parent, false))
            
            ITEM_VIEW_TYPE -> return ItemViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_entrada_item, parent, false))
            
            else -> super.createViewHolder(parent, viewType)
        }
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val current = getItem(position)) {
            is UIModel.Separator -> (holder as SeparatorViewHolder).bindSeparator(current)
            is UIModel.Item -> (holder as ItemViewHolder).bindView(current)
        }
    }
    
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UIModel.Separator -> SEPARATOR_VIEW_TYPE
            is UIModel.Item -> ITEM_VIEW_TYPE
            else -> super.getItemViewType(position)
        }
    }
    
    fun setClickListener(
        onItemLongClick: ((View, Entrada) -> Unit)? = null,
        onSeparatorClick: (() -> Unit)? = null,
    ) {
        this.onItemLongClick = onItemLongClick
        this.onSeparatorClick = onSeparatorClick
    }
    
    private inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = AdapterEntradaItemBinding.bind(itemView)
        
        fun bindView(itemModel: UIModel.Item) {
            binding.textviewValor.text = Utils.formatCurrency(itemModel.entrada.valor)
            binding.textviewDescricao.text = itemModel.entrada.descricao
            binding.textviewData.text = itemModel.entrada.data.formattedDate()
            
            itemView.setOnLongClickListener { anchor ->
                onItemLongClick?.invoke(anchor, itemModel.entrada)
                true
            }
        }
    }
    
    private inner class SeparatorViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val binding = AdapterEntradaSeparatorBinding.bind(itemView)
        
        fun bindSeparator(separatorModel: UIModel.Separator) {
            binding.textviewMes.text = separatorModel.mes
    
            itemView.setOnClickListener {
                onSeparatorClick?.invoke()
            }
        }
    }
    
    sealed class UIModel {
        data class Item(val entrada: Entrada): UIModel()
        data class Separator(val mes: String): UIModel()
    }
    
    companion object {
        private val UIMODEL_COMPARATOR = object: DiffUtil.ItemCallback<UIModel>()  {
            override fun areItemsTheSame(oldItem: UIModel, newItem: UIModel): Boolean {
                return (oldItem is UIModel.Item && newItem is UIModel.Item && oldItem.entrada.id == oldItem.entrada.id)
                        || (oldItem is UIModel.Separator && newItem is UIModel.Separator && oldItem.mes == newItem.mes)
            }
            override fun areContentsTheSame(oldItem: UIModel, newItem: UIModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
