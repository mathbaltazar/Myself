package br.com.myself.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.myself.R
import br.com.myself.domain.entity.Despesa
import br.com.myself.domain.repository.DespesaRepository
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.ui.financas.despesas.DetalhesDespesaActivity
import br.com.myself.util.Async
import br.com.myself.util.Utils
import kotlinx.android.synthetic.main.adapter_despesas_item.view.*

class DespesaAdapter(private val repository: DespesaRepository)
    : ListAdapter<Despesa, RecyclerView.ViewHolder>(COMPARATOR) {
    
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
                excluirDepesa(despesa)
            }
            
            // BOTÃO DE AÇÃO DETALHES
            itemView.button_adapter_despesas_item_detalhes.setOnClickListener {
                val intent = Intent(itemView.context, DetalhesDespesaActivity::class.java)
                intent.putExtra(DetalhesDespesaActivity.DESPESA_ID, despesa.id)
                itemView.context.startActivity(intent)
            }
            
            // BOTÃO DE AÇÃO REGISTRAR
            itemView.button_adapter_despesas_item_registrar.setOnClickListener {
                Trigger.launch(Events.RegistrarDespesa(despesa))
            }
        }
    
        private fun excluirDepesa(despesa: Despesa) {
            var mensagem = "Nome: ${despesa.nome}"
            mensagem += "\nValor: ${Utils.formatCurrency(despesa.valor)}"
            if (despesa.diaVencimento != 0) mensagem += "\nVencimento: ${despesa.diaVencimento}"
            
            AlertDialog.Builder(itemView.context).setTitle("Excluir despesa?")
                .setMessage(mensagem)
                .setPositiveButton("Excluir") { _, _ ->
                    Async.doInBackground({
                        repository.excluir(despesa)
                    }) {
                        Toast.makeText(itemView.context, "Removido!", Toast.LENGTH_SHORT).show()
                        Trigger.launch(Events.UpdateDespesas)
                    }
                }.setNegativeButton("Cancelar", null)
                .show()
        }
    
    }
}
