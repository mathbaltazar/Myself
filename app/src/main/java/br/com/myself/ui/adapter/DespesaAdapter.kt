package br.com.myself.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.myself.R
import br.com.myself.domain.entity.Despesa
import br.com.myself.util.Utils
import kotlinx.android.synthetic.main.adapter_despesas_item.view.*

class DespesaAdapter : ListAdapter<Despesa, RecyclerView.ViewHolder>(COMPARATOR) {
    
    private var mListener: ((Int, Despesa) -> Unit)? = null
    
    private object COMPARATOR : DiffUtil.ItemCallback<Despesa>() {
        override fun areItemsTheSame(oldItem: Despesa, newItem: Despesa): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Despesa, newItem: Despesa): Boolean = oldItem == newItem
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_despesas_item, parent, false)
        )
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bindView(getItem(position))
    }
    
    fun setOnItemActionListener(listener: (Int, Despesa) -> Unit) {
        mListener = listener
    }
    
    private inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        fun bindView(despesa: Despesa) {
            
            // NOME
            itemView.tv_adapter_despesas_item_nome.text = despesa.nome
            
            // TODO ULTIMO REGISTRO
            /*val ultimoRegistro = RegistroContext.getDAO(itemView.context).getDataUltimoRegistro(despesa.id)
            itemView.tv_adapter_despesas_item_ultimo_registro.text =
                if (ultimoRegistro == 0L) "Não há registros."
                else "Último registro: ${ultimoRegistro.formattedDate()}"*/
            
            // DIA VENCIMENTO
            if (despesa.diaVencimento != 0) {
                itemView.ll_adapter_despesas_item_vencimento.visibility = View.VISIBLE
                itemView.tv_adapter_despesas_item_dia_vencimento.text = despesa.diaVencimento.toString()
            } else {
                itemView.ll_adapter_despesas_item_vencimento.visibility = View.GONE
            }
    
            // VALOR
            if (despesa.valor > 0.0) {
                itemView.tv_adapter_despesas_item_valor.text = Utils.formatCurrency(despesa.valor)
            } else {
                itemView.tv_adapter_despesas_item_valor.visibility = View.GONE
            }
            
            // BOTÃO DE AÇÃO EXCLUIR
            itemView.button_adapter_despesas_item_excluir.setOnClickListener {
                mListener?.invoke(ACTION_EXCLUIR, despesa)
            }
            
            // BOTÃO DE AÇÃO DETALHES
            itemView.button_adapter_despesas_item_detalhes.setOnClickListener {
                mListener?.invoke(ACTION_DETALHES, despesa)
            }
            
            // BOTÃO DE AÇÃO REGISTRAR
            itemView.button_adapter_despesas_item_registrar.setOnClickListener {
                mListener?.invoke(ACTION_REGISTRAR, despesa)
            }
        }
    
    }
    
    companion object {
        const val ACTION_EXCLUIR = 1
        const val ACTION_DETALHES = 2
        const val ACTION_REGISTRAR = 3
    }
}
