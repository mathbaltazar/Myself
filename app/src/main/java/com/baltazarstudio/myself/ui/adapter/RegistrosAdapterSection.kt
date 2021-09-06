package com.baltazarstudio.myself.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.myself.R
import com.baltazarstudio.myself.context.RegistroContext
import com.baltazarstudio.myself.model.Registro
import com.baltazarstudio.myself.observer.Trigger
import com.baltazarstudio.myself.observer.Events
import com.baltazarstudio.myself.ui.registros.DetalhesRegistroDialog
import com.baltazarstudio.myself.util.Utils
import com.baltazarstudio.myself.util.Utils.Companion.formattedDate
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.layout_section_header_registro.view.*
import kotlinx.android.synthetic.main.layout_section_item_registro.view.*

class RegistrosAdapterSection(
    private val adapter: SectionedRecyclerViewAdapter,
    private val ano: Int,
    private val mes: Int,
    private val registros: List<Registro>
) : Section(
    SectionParameters.builder().headerResourceId(R.layout.layout_section_header_registro)
        .itemResourceId(R.layout.layout_section_item_registro).build()
) {
    
    
    private var mOnCheckableModeItemSelectedListener: (Int) -> Unit = {}
    var checkableMode: Boolean = false
    private var expanded: Boolean = true
    private var multiSelectModeEnabled = true
    
    
    fun setOnCheckableModeItemSelectedListener(listener: (Int) -> Unit) {
        mOnCheckableModeItemSelectedListener = listener
    }
    
    fun disableMultiSelectMode() { multiSelectModeEnabled = false }
    
    override fun getContentItemsTotal(): Int {
        return if (expanded) registros.size else 0
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
        (holder as ItemViewHolder).bindView(registros[position])
    }
    
    private inner class HeaderViewHolder(headerView: View) : RecyclerView.ViewHolder(headerView) {
        fun bindView() {
            var total = 0.0
            for (movimento in registros) total += movimento.valor
            itemView.tv_section_header_registros_title.text = Utils.getMesString(mes, ano)
            itemView.tv_section_header_registros_total.text = Utils.formatCurrency(total)
            
            if (expanded)
                itemView.iv_section_header_registros_expand.setImageResource(R.drawable.ic_arrow_up)
            else
                itemView.iv_section_header_registros_expand.setImageResource(R.drawable.ic_arrow_down)
            
            itemView.setOnClickListener {
                expanded = !expanded
                adapter.notifyDataSetChanged()
            }
        }
    }
    
    private inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        fun bindView(registro: Registro) {
            
            itemView.tv_registro_descricao.text = registro.descricao
            itemView.tv_registro_valor.text = Utils.formatCurrency(registro.valor)
            itemView.tv_registro_data.text = registro.data!!.formattedDate()
            
            if (!registro.local.isNullOrBlank()) {
                itemView.tv_registro_local.visibility = View.VISIBLE
                itemView.tv_registro_local.text = registro.local
            } else {
                itemView.tv_registro_local.visibility = View.GONE
            }
    
    
            if (!checkableMode) {
                unselectItem(registro)
            }
            
            itemView.setOnClickListener {
                if (!checkableMode) {
                    DetalhesRegistroDialog(itemView.context, registro)
                } else {
                    if (!RegistroContext.registrosParaExcluir.contains(registro)) {
                        selectItem(registro)
                    } else {
                        unselectItem(registro)
                    }
                    
                    mOnCheckableModeItemSelectedListener(RegistroContext.registrosParaExcluir.size)
                }
            }
            
            itemView.setOnLongClickListener {
                if (multiSelectModeEnabled) {
                    if (!checkableMode) {
                        selectItem(registro)
                        checkableMode = true
                        Trigger.launch(Events.HabilitarModoMultiSelecao())
                    }
                }
                
                return@setOnLongClickListener checkableMode
            }
            
        }
    
        private fun selectItem(registro: Registro) {
            itemView.setBackgroundResource(R.drawable.background_section_item_selected)
            RegistroContext.registrosParaExcluir.add(registro)
        }
    
        private fun unselectItem(registro: Registro) {
            itemView.setBackgroundResource(android.R.drawable.screen_background_light)
            RegistroContext.registrosParaExcluir.remove(registro)
        }
    }
    
}
