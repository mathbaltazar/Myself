package com.baltazarstudio.regular.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.MovimentoContext
import com.baltazarstudio.regular.model.Movimento
import com.baltazarstudio.regular.observer.Trigger
import com.baltazarstudio.regular.observer.TriggerEvent
import com.baltazarstudio.regular.ui.registros.movimentos.DetalhesMovimentoDialog
import com.baltazarstudio.regular.ui.registros.movimentos.RegistrarMovimentoDialog
import com.baltazarstudio.regular.util.Utils
import com.baltazarstudio.regular.util.Utils.Companion.formattedDate
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.layout_section_header_movimento.view.*
import kotlinx.android.synthetic.main.layout_section_item_movimento.view.*

class MovimentosAdapterSection(private val adapter: SectionedRecyclerViewAdapter,
                               private val ano: Int, private val mes: Int, private val movimentos: List<Movimento>
) : Section(SectionParameters.builder()
        .headerResourceId(R.layout.layout_section_header_movimento)
        .itemResourceId(R.layout.layout_section_item_movimento)
        .build()) {
    
    
    private var mCheckableModeActiveListener: () -> Unit = {}
    private var mOnCheckableModeItemClickedListener: () -> Unit = {}
    
    var checkableMode: Boolean = false
        set(checkable) {
            field = checkable
            adapter.notifyAllItemsChangedInSection(this)
    
            if (!checkable) {
                MovimentoContext.movimentosParaExcluir.clear()
            }
            
        }
    
    
    fun setOnCheckableModeActiveListener(listener: () -> Unit) {
        mCheckableModeActiveListener = listener
    }
    
    fun setOnCheckableModeItemClickedListener(listener: () -> Unit) {
        mOnCheckableModeItemClickedListener = listener
    }
    
    override fun getContentItemsTotal(): Int {
        return movimentos.size
    }
    
    override fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder {
        return HeaderViewHolder(view)
    }
    
    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
        (holder as HeaderViewHolder).bindView()
    }
    
    override fun getItemViewHolder(view: View): RecyclerView.ViewHolder {
        return ItemViewHolder(view)
    }
    
    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as ItemViewHolder).bindView(movimentos[position])
    }
    
    private inner class HeaderViewHolder(headerView: View) : RecyclerView.ViewHolder(headerView) {
        fun bindView() {
            var total = 0.0
            for (movimento in movimentos) total += movimento.valor
            itemView.tv_header_movimento_title.text = Utils.getMesString(mes, ano)
            itemView.tv_header_movimento_total.text = Utils.formatCurrency(total)
        }
    }
    
    private inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        fun bindView(movimento: Movimento) {
            
            itemView.tv_movimento_descricao.text = movimento.descricao
            itemView.tv_movimento_valor.text = Utils.formatCurrency(movimento.valor)
            itemView.tv_movimento_data.text = movimento.data!!.formattedDate()
            
            itemView.checkbox_movimento.setOnCheckedChangeListener { _, isChecked ->
                if (checkableMode) {
                    if (isChecked) {
                        MovimentoContext.movimentosParaExcluir.add(movimento)
                    } else {
                        MovimentoContext.movimentosParaExcluir.remove(movimento)
                    }
                    mOnCheckableModeItemClickedListener()
                }
            }
            
            if (checkableMode) {
                itemView.checkbox_movimento.visibility = View.VISIBLE
            } else {
                itemView.checkbox_movimento.visibility = View.GONE
            }
            
            itemView.setOnClickListener {
                if (!checkableMode) {
                    DetalhesMovimentoDialog(itemView.context, movimento)
                } else {
                    itemView.checkbox_movimento.isChecked = !itemView.checkbox_movimento.isChecked
                }
            }
            
            itemView.setOnLongClickListener {
                if (!checkableMode) {
                    itemView.checkbox_movimento.isChecked = true
                    checkableMode = true
                    Trigger.launch(TriggerEvent.PrepareMultiChoiceRegistrosLayout(false))
                    mCheckableModeActiveListener()
                }
                
                return@setOnLongClickListener checkableMode
            }
            
        }
    }
    
}
