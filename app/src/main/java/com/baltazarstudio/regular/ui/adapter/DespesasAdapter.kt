package com.baltazarstudio.regular.ui.adapter

import android.content.Context
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.DespesaContext
import com.baltazarstudio.regular.context.RegistroContext
import com.baltazarstudio.regular.model.Despesa
import com.baltazarstudio.regular.observer.Events
import com.baltazarstudio.regular.observer.Trigger
import com.baltazarstudio.regular.ui.despesa.CriarDespesaDialog
import com.baltazarstudio.regular.ui.despesa.DetalhesDespesaActivity
import com.baltazarstudio.regular.ui.despesa.RegistrarDespesaDialog
import com.baltazarstudio.regular.util.Utils
import com.baltazarstudio.regular.util.Utils.Companion.formattedDate
import kotlinx.android.synthetic.main.layout_section_item_despesa.view.*
import org.jetbrains.anko.startActivity
import java.util.*

class DespesasAdapter(context: Context, private val despesas: ArrayList<Despesa>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    private val layoutInflater = LayoutInflater.from(context)
    
    override fun getItemCount(): Int {
        return despesas.size
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            layoutInflater.inflate(R.layout.layout_section_item_despesa, parent, false)
        )
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bindView(position)
    }
    
    private inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(position: Int) {
            val despesa = despesas[position]
            
            itemView.setOnClickListener {
                DespesaContext.despesaDetalhada = despesa
                itemView.context.startActivity<DetalhesDespesaActivity>()
            }
            
            // NOME
            itemView.tv_section_item_despesas_nome.text = despesa.nome
            
            // ULTIMO REGISTRO
            val ultimoRegistro = RegistroContext.getDAO(itemView.context).getUltimoRegistro(despesa.codigo!!)
            if (ultimoRegistro == 0L) {
                itemView.tv_section_item_despesas_ultimo_registro.text = "Não há registros."
            } else {
                itemView.tv_section_item_despesas_ultimo_registro.text = "Último registro: ${ultimoRegistro.formattedDate()}"
            }
            
            // DIA VENCIMENTO
            if (despesa.diaVencimento != 0) {
                itemView.ll_section_item_despesas_vencimento.visibility = View.VISIBLE
                itemView.tv_section_item_despesas_dia_vencimento.text = despesa.diaVencimento.toString()
            } else {
                itemView.ll_section_item_despesas_vencimento.visibility = View.GONE
            }
    
            // VALOR
            itemView.tv_section_item_despesas_valor.text = Utils.formatCurrency(despesa.valor)
            
            // OPÇOES
            itemView.iv_section_item_despesas_opcoes.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, it)
                popupMenu.menu.add(Menu.NONE, 0, Menu.NONE, "Excluir")
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        1 -> excluirDepesa(despesa)
                    }
                    false
                }
                popupMenu.show()
            }
            
            // ESCONDER ÚLTIMO DIVIDER
            if (despesa == despesas.last()) {
                itemView.divider_section_item_despesas.visibility = View.GONE
            }
        }
    
        private fun excluirDepesa(despesa: Despesa) {
            AlertDialog.Builder(itemView.context).setTitle("Excluir")
                .setMessage("Deseja realmente deletar esta despesa?")
                .setPositiveButton("Excluir") { _, _ ->
                    DespesaContext.getDAO(itemView.context).deletar(despesa)
                    Trigger.launch(Events.Toast("Removido!"))
                    //Trigger.launch(Events.UpdateDespesas())
                    notifyItemRemoved(adapterPosition)
                }.setNegativeButton("Cancelar", null)
                .show()
        }
    
    }
}
