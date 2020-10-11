package com.baltazarstudio.regular.ui.registros.movimentos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.MovimentoContext
import com.baltazarstudio.regular.model.Movimento
import com.baltazarstudio.regular.ui.adapter.MovimentosSectionAdapter
import com.baltazarstudio.regular.util.Utils.Companion.formattedDate
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_movimentos.view.*

class MovimentosFragment : Fragment() {
    
    private lateinit var mView: View
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_movimentos, container, false)
        return mView
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    
    
        carregarMovimentos()
    
        mView.fab_add_movimento.setOnClickListener {
            adicionarMovimento()
        }
    }
    
    
    fun carregarMovimentos(pesquisa: String? = null) {
        val mDAO = MovimentoContext.getDAO(mView.context)
        val itensMovimentos: List<Movimento>
        if (pesquisa.isNullOrBlank()) {
            itensMovimentos = mDAO.getTodosMovimentos()
            mView.fab_add_movimento.visibility = View.VISIBLE
        } else {MovimentoContext.getDAO(mView.context)
            itensMovimentos = mDAO.getTodosMovimentos(pesquisa.trim())
            mView.fab_add_movimento.visibility = View.GONE
        }
        
        if (itensMovimentos.isNotEmpty()) {
            mView.tv_movimentos_empty.visibility = View.GONE
            mView.rv_movimentos.visibility = View.VISIBLE
            
            val adapter = SectionedRecyclerViewAdapter()
            
            for (ano in getAnosDisponiveis(itensMovimentos)) {
                for (mes in getMesDisponivelPorAno(itensMovimentos, ano)) {
                    val itens = filtrarItensPorData(itensMovimentos, mes, ano)
                    
                    if (itens.isEmpty()) continue
                    
                    adapter.addSection(MovimentosSectionAdapter(ano, mes, itens))
                }
            }
            
            mView.rv_movimentos.adapter = adapter
            mView.rv_movimentos.layoutManager = LinearLayoutManager(mView.context)
            
        } else {
            mView.tv_movimentos_empty.visibility = View.VISIBLE
            mView.rv_movimentos.visibility = View.GONE
        }
        
        
    }
    
    private fun getAnosDisponiveis(itens: List<Movimento>): Collection<Int> {
        val anos = itens.map { it.data?.formattedDate()?.substring(6) }
        return anos.distinct().map { it!!.toInt() }
    }
    
    private fun getMesDisponivelPorAno(itens: List<Movimento>, ano: Int): Collection<Int> {
        val meses = itens
            .filter { it.data?.formattedDate()?.substring(6) == ano.toString() }
            .map {
                it.data?.formattedDate()?.substring(3, 5)
            }
        return meses.distinct().map { it!!.toInt() }
    }
    
    private fun filtrarItensPorData(itens: List<Movimento>, mes: Int, ano: Int): List<Movimento> {
        return itens
            .filter { it.data?.formattedDate()?.substring(6) == ano.toString()
                    && it.data?.formattedDate()?.substring(3, 5)?.toInt() == mes }
        
    }
    
    fun adicionarMovimento() {
        val dialog =
            RegistrarMovimentoDialog(
                mView.context
            )
        dialog.show()
    }
}
