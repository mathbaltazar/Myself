package br.com.myself.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import br.com.myself.R
import br.com.myself.context.DespesaContext
import br.com.myself.model.entity.Despesa
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.ui.financas.despesas.DetalhesDespesaActivity
import br.com.myself.util.Utils
import kotlinx.android.synthetic.main.adapter_despesas_item.view.*

class DespesasAdapter(context: Context)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    private val layoutInflater = LayoutInflater.from(context)
    private val listaDespesas = DespesaContext.getDataView(context).despesas
    
    override fun getItemCount(): Int {
        return listaDespesas.size
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            layoutInflater.inflate(R.layout.adapter_despesas_item, parent, false)
        )
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bindView(listaDespesas[position], this)
    }
    
    private class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var expanded: Boolean = false
        
        fun bindView(despesa: Despesa, adapter: DespesasAdapter) {
            
            itemView.ll_adapter_despesas_item_container.setOnClickListener {
                expanded = !expanded
                adapter.notifyDataSetChanged()
            }
            
            // NOME
            itemView.tv_adapter_despesas_item_nome.text = despesa.nome
            
            // ULTIMO REGISTRO
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
    
            
            // lAYOUT EXPANDIDO
            if (expanded) {
                itemView.iv_adapter_despesas_item_expanded.setImageResource(R.drawable.ic_arrow_up) // SETA
                itemView.layout_adapter_despesas_item_acoes.visibility = View.VISIBLE // BOTÕES
                itemView.divider_adapter_despesas_item.visibility = View.VISIBLE // DIVIDER
            } else {
                itemView.iv_adapter_despesas_item_expanded.setImageResource(R.drawable.ic_arrow_down) // SETA
                itemView.layout_adapter_despesas_item_acoes.visibility = View.GONE // BOTÕES
                itemView.divider_adapter_despesas_item.visibility = View.GONE // DIVIDER
            }
            
            // BOTÃO DE AÇÃO EXCLUIR
            itemView.button_adapter_despesas_item_excluir.setOnClickListener {
                excluirDepesa(despesa)
            }
            
            // BOTÃO DE AÇÃO DETALHES
            itemView.button_adapter_despesas_item_detalhes.setOnClickListener {
                DespesaContext.getDataView(it.context).despesaDetalhada = despesa
                itemView.context.startActivity(Intent(itemView.context, DetalhesDespesaActivity::class.java))
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
                    
                    DespesaContext.getDAO(itemView.context).deletar(despesa)
                    DespesaContext.removerDespesa(despesa)
    
                    Toast.makeText(itemView.context, "Removido!", Toast.LENGTH_SHORT).show()
                    Trigger.launch(Events.UpdateDespesas)
                }.setNegativeButton("Cancelar", null)
                .show()
        }
    
    }
}
