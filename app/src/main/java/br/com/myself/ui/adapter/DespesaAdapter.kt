package br.com.myself.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.myself.R
import br.com.myself.databinding.AdapterDespesasItemBinding
import br.com.myself.domain.entity.Despesa
import br.com.myself.util.Utils

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
        val binding = AdapterDespesasItemBinding.bind(itemView)
        
        fun bindView(despesa: Despesa) {
            
            // NOME
            binding.textviewNome.text = despesa.nome
            
            // TODO ULTIMO REGISTRO
            /*val ultimoRegistro = RegistroContext.getDAO(itemView.context).getDataUltimoRegistro(despesa.id)
            itemView.tv_adapter_despesas_item_ultimo_registro.text =
                if (ultimoRegistro == 0L) "Não há registros."
                else "Último registro: ${ultimoRegistro.formattedDate()}"*/
            
            // DIA VENCIMENTO
            if (despesa.diaVencimento != 0) {
                binding.layoutVencimento.visibility = View.VISIBLE
                binding.textviewVencimento.text = despesa.diaVencimento.toString()
            } else {
                binding.layoutVencimento.visibility = View.GONE
            }
    
            // VALOR
            if (despesa.valor > 0.0) {
                binding.textviewValor.text = Utils.formatCurrency(despesa.valor)
            } else {
                binding.textviewValor.visibility = View.GONE
            }
            
            // BOTÃO DE AÇÃO EXCLUIR
            binding.buttonExcluir.setOnClickListener {
                mListener?.invoke(ACTION_EXCLUIR, despesa)
            }
            
            // BOTÃO DE AÇÃO DETALHES
            binding.buttonDetalhes.setOnClickListener {
                mListener?.invoke(ACTION_DETALHES, despesa)
            }
            
            // BOTÃO DE AÇÃO REGISTRAR
            binding.buttonRegistrar.setOnClickListener {
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
