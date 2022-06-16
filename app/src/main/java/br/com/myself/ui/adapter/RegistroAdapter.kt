package br.com.myself.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.myself.R
import br.com.myself.databinding.AdapterRegistroBinding
import br.com.myself.data.model.Registro
import br.com.myself.util.AdapterClickListener
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.formattedDate

class RegistroAdapter : ListAdapter<Registro, RecyclerView.ViewHolder>(COMPARATOR) {
    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<Registro>() {
            override fun areItemsTheSame(oldItem: Registro, newItem: Registro): Boolean =
                oldItem.id == newItem.id
        
            override fun areContentsTheSame(oldItem: Registro, newItem: Registro): Boolean =
                oldItem == newItem
        }
    }
    
    private var mListener: AdapterClickListener<Registro>? = null
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RegistroViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_registro, parent,false))
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as RegistroViewHolder).bind(getItem(position))
    }
    
    fun setClickListener(listener: AdapterClickListener<Registro>) {
        mListener = listener
    }
    
    private inner class RegistroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = AdapterRegistroBinding.bind(itemView)
        
        fun bind(registro: Registro) {
    
            binding.textviewDescricao.text = registro.descricao
            binding.textviewValor.text = Utils.formatCurrency(registro.valor)
            binding.textviewData.text = registro.data.formattedDate()
            binding.textviewOutros.text = registro.outros
    
            binding.textviewOutros.visibility =
                if (registro.outros.isNullOrBlank()) View.GONE else View.VISIBLE
    
            binding.imageviewIconeDespesa.visibility =
                if (registro.despesa_id == null || registro.despesa_id == 0L) View.INVISIBLE else View.VISIBLE
    
            //TODO HABILITAR ÍCONE QUANDO REGISTRO HOUVER ANEXO
            /*itemView.iv_icon_registro_item_anexo.visibility =
                if (registro.anexo == null) View.GONE else View.VISIBLE*/
    
            itemView.setOnClickListener {
                mListener?.let { it.onClick(registro) }
            }
    
            itemView.setOnLongClickListener {
                mListener?.let { it.onLongClick(registro) }
                true
            }
    
        }
    }
    
}
