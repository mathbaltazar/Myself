package com.baltazarstudio.myself.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.myself.R
import com.baltazarstudio.myself.context.DespesaContext
import com.baltazarstudio.myself.context.RegistroContext
import com.baltazarstudio.myself.model.Despesa
import com.baltazarstudio.myself.model.enum.Boolean
import com.baltazarstudio.myself.observer.Events
import com.baltazarstudio.myself.observer.Trigger
import com.baltazarstudio.myself.util.Utils
import com.baltazarstudio.myself.util.Utils.Companion.formattedDate
import kotlinx.android.synthetic.main.layout_section_item_despesa_arquivada.view.*

class DespesasArquivadasAdapter(context: Context, val listaDespesas: MutableList<Despesa>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    private var mChangedListener: OnStateChangedListener? = null
    private val layoutInflater = LayoutInflater.from(context)
    
    override fun getItemCount(): Int {
        return listaDespesas.count()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(layoutInflater.inflate(R.layout.layout_section_item_despesa_arquivada,
            parent,
            false))
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bindView(listaDespesas[position])
    }
    
    fun setOnStateChangedListener(listener: OnStateChangedListener) {
        mChangedListener = listener
    }
    
    private inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(despesa: Despesa) {
            
            itemView.tv_section_item_despesa_arquivada_nome.text = despesa.nome
            itemView.tv_section_item_despesa_arquivada_valor.text =
                Utils.formatCurrency(despesa.valor)
            
            val regDao = RegistroContext.getDAO(itemView.context)
            
            val qtdRegistros = regDao.getRegistrosFiltradosPelaDespesa(despesa.codigo).size
            itemView.tv_section_item_despesa_arquivada_qtd_registro.text =
                "Quantidade de registros: ${if (qtdRegistros == 0) "Nenhum" else qtdRegistros}"
            
            val ultimoRegistro = regDao.getUltimoRegistro(despesa.codigo)
            if (ultimoRegistro == 0L) {
                itemView.tv_section_item_despesa_arquivada_ultimo_registro.text =
                    "Não há registros."
            } else {
                itemView.tv_section_item_despesa_arquivada_ultimo_registro.text =
                    "Último registro: ${ultimoRegistro.formattedDate()}"
            }
            
            
            if (despesa.diaVencimento > 0) {
                itemView.tv_section_item_despesa_arquivada_vencimento.text =
                    despesa.diaVencimento.toString()
            } else {
                itemView.tv_section_item_despesa_arquivada_vencimento.visibility = View.GONE
            }
            
            // OPÇOES
            itemView.iv_section_item_despesa_arquivada_opcoes.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, it)
                popupMenu.menu.add(Menu.NONE, 0, Menu.NONE, "Excluir")
                popupMenu.menu.add(Menu.NONE, 1, Menu.NONE, "Restaurar")
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        0 -> excluirDepesa(despesa)
                        1 -> restaurar(despesa)
                    }
                    false
                }
                popupMenu.show()
            }
        }
        
        private fun restaurar(despesa: Despesa) {
            despesa.arquivado = Boolean.FALSE
            
            DespesaContext.getDAO(itemView.context).alterar(despesa)
            Trigger.launch(Events.Toast("Restaurada!"), Events.UpdateDespesas())
            
            mChangedListener?.onChanged(despesa)
        }
        
        private fun excluirDepesa(despesa: Despesa) {
            AlertDialog.Builder(itemView.context).setTitle("Excluir")
                .setMessage("Deseja realmente excluir?").setPositiveButton("Excluir") { _, _ ->
                    DespesaContext.getDAO(itemView.context).deletar(despesa)
                    Trigger.launch(Events.Snack("Removido!"))
        
                    mChangedListener?.onChanged(despesa)
                }.setNegativeButton("Cancelar", null).show()
        }
    }
    
    interface OnStateChangedListener {
        fun onChanged(despesa: Despesa)
    }
}
