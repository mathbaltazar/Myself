package br.com.myself.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.myself.R
import br.com.myself.model.entity.Registro
import br.com.myself.util.AdapterClickListener
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.formattedDate
import kotlinx.android.synthetic.main.layout_adapter_registro.view.*

class RegistroAdapter : ListAdapter<Registro, RecyclerView.ViewHolder>(COMPARATOR) {
    
    private object COMPARATOR : DiffUtil.ItemCallback<Registro>() {
        override fun areItemsTheSame(oldItem: Registro, newItem: Registro): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Registro, newItem: Registro): Boolean = oldItem == newItem
    }
    
    private var mListener: AdapterClickListener<Registro>? = null
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RegistroViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_adapter_registro, parent,false))
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as RegistroViewHolder).bind(getItem(position))
    }
    
    fun setClickListener(listener: AdapterClickListener<Registro>) {
        mListener = listener
    }
    
    private inner class RegistroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(registro: Registro) {
            
            itemView.tv_registro_descricao.text = registro.descricao
            itemView.tv_registro_valor.text = Utils.formatCurrency(registro.valor)
            itemView.tv_registro_data.text = registro.data.formattedDate()
            itemView.tv_registro_outros.text = registro.outros
            
            itemView.tv_registro_outros.visibility =
                if (registro.outros.isNullOrBlank()) View.GONE else View.VISIBLE
            
            itemView.iv_icon_registro_item_despesa_atrelada.visibility =
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
