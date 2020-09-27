package com.baltazarstudio.regular.controller

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.regular.database.dao.GastoDAO
import com.baltazarstudio.regular.model.Gasto
import com.baltazarstudio.regular.ui.adapter.GastoAdapter
import com.baltazarstudio.regular.ui.movimentos.GastoDialog
import kotlinx.android.synthetic.main.layout_page_movimentos_gastos.view.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

class GastosController(private val view: View) {
    
    private val mDAO: GastoDAO = GastoDAO(view.context)
    
    fun init() {
        carregarGastos()
        
        view.fab_add_movimento.setOnClickListener {
            adicionarGasto()
        }
    }
    
    fun carregarGastos(pesquisa: String? = null) {
        val itensGastos: List<Gasto>
        if (pesquisa.isNullOrBlank()) {
            itensGastos = mDAO.getTodosGastos()
            view.fab_add_movimento.visibility = View.VISIBLE
        } else {
            itensGastos = mDAO.getTodosGastos(pesquisa.trim())
            view.fab_add_movimento.visibility = View.GONE
        }
        
        view.ll_movimentos.removeAllViews()
        
        if (itensGastos.isNotEmpty()) {
            val lp = ViewGroup.LayoutParams(matchParent, wrapContent)
            
            for (ano in mDAO.getAnosDisponiveis()) {
                for (mes in mDAO.getMesDisponivelPorAno(ano)) {
                    val itens = itensGastos.filter { it.mes == mes && it.ano == ano }
                    
                    if (itens.isEmpty()) continue
                    
                    val recyclerView = RecyclerView(view.context)
                    recyclerView.layoutParams = lp
                    
                    val adapter = GastoAdapter(view.context, Pair(mes, ano), itens, this)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(view.context)
                    
                    recyclerView.addItemDecoration(
                        DividerItemDecoration(
                            view.context, DividerItemDecoration.VERTICAL
                        )
                    )
                    
                    view.ll_movimentos.addView(recyclerView)
                }
            }
        }
        
        
    }
    
    fun adicionarGasto() {
        val dialog = GastoDialog(view.context, this)
        dialog.show()
    }
    
    fun editarGasto(gasto: Gasto) {
        val dialog = GastoDialog(view.context, this).edit(gasto)
        dialog.show()
    }
    
    fun inserir(gasto: Gasto) {
        mDAO.inserir(gasto)
    }
    
    fun alterar(gasto: Gasto) {
        mDAO.alterar(gasto)
    }
    
    fun excluir(gasto: Gasto) {
        mDAO.excluir(gasto = gasto)
        
        // TODO Atualizar últimos registros das despesas
    }
    
}