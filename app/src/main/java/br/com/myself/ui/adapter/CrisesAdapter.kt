package br.com.myself.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.myself.R
import br.com.myself.databinding.AdapterCrisesItemBinding
import br.com.myself.domain.entity.Crise
import br.com.myself.util.Utils.Companion.formattedDate

class CrisesAdapter : ListAdapter<Crise, RecyclerView.ViewHolder>(COMPARATOR) {
    
    private object COMPARATOR : DiffUtil.ItemCallback<Crise>() {
        override fun areItemsTheSame(oldItem: Crise, newItem: Crise): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Crise, newItem: Crise): Boolean = oldItem == newItem
    }
    
    private var _onClick: ((Crise, View) -> Unit)? = null
    
    
    fun setOnItemClickListener(onClick : (Crise,View) -> Unit) {
        this._onClick = onClick
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_crises_item, parent, false))
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bindView(getItem(position))
    }
    
    private inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = AdapterCrisesItemBinding.bind(itemView)
        
        fun bindView(crise: Crise) {
            itemView.setOnClickListener { _onClick?.invoke(crise, itemView) }
    
            binding.textviewData.text = crise.data.formattedDate()
            binding.textviewHorario1.text = crise.horario1
            binding.textviewHorario2.text = crise.horario2
            binding.textviewObservacoes.text =
                if (crise.observacoes.isBlank()) "Nada registrado." else crise.observacoes
        }
    }
}
