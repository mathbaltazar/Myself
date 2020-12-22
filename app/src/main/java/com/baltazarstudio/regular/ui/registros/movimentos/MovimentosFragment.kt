package com.baltazarstudio.regular.ui.registros.movimentos

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.MovimentoContext
import com.baltazarstudio.regular.model.Movimento
import com.baltazarstudio.regular.observer.Trigger
import com.baltazarstudio.regular.observer.TriggerEvent
import com.baltazarstudio.regular.ui.adapter.MovimentosAdapterSection
import com.baltazarstudio.regular.util.Utils.Companion.formattedDate
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_movimentos.*
import kotlinx.android.synthetic.main.fragment_movimentos.view.*

class MovimentosFragment : Fragment() {
    
    private lateinit var mView: View
    private var multiChoiceToolbarActionMode: androidx.appcompat.view.ActionMode? = null
    private lateinit var callback: androidx.appcompat.view.ActionMode.Callback
    
    private val disposable = CompositeDisposable()
    
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
        registerMultiSelectionCallbacks()
    
        mView.fab_add_movimento.setOnClickListener {
            val dialog = RegistrarMovimentoDialog(mView.context)
            dialog.show()
        }
    
        callback = object : androidx.appcompat.view.ActionMode.Callback {
            
            override fun onCreateActionMode(
                mode: androidx.appcompat.view.ActionMode?, menu: Menu?
            ): Boolean {
                requireActivity().menuInflater.inflate(R.menu.menu_contextual_multi_select, menu)
                return true
            }
    
            override fun onPrepareActionMode(
                mode: androidx.appcompat.view.ActionMode?, menu: Menu?
            ): Boolean {
                return false
            }
    
            override fun onActionItemClicked(
                mode: androidx.appcompat.view.ActionMode?, item: MenuItem?): Boolean {
                when (item?.itemId) {
                    R.id.action_delete_movimentos -> {
                        if (MovimentoContext.movimentosParaExcluir.size == 0) {
                            Trigger.launch(TriggerEvent.Toast("Não há movimentos selecionados"))
                        } else {
                            AlertDialog.Builder(mView.context).setTitle("Excluir")
                                .setMessage("Confirma a exclusão dos itens selecionados?")
                                .setPositiveButton("Excluir") { _, _ ->
                                    MovimentoContext.excluirMovimentos(mView.context)
                                    desabilitarModoSelecao()
                            
                                    Trigger.launch(TriggerEvent.Snack("Registros Removidos!"))
                                    Trigger.launch(TriggerEvent.UpdateTelaMovimento())
                                    Trigger.launch(TriggerEvent.UpdateTelaDespesa())
                                }.setNegativeButton("Cancelar", null).show()
                        }
                    }
                }
        
                return true
            }
    
            override fun onDestroyActionMode(mode: androidx.appcompat.view.ActionMode?) {
                multiChoiceToolbarActionMode = null
                Trigger.launch(TriggerEvent.DesabilitarModoMultiSelecao())
            }
        }
        
    }
    
    private fun registerMultiSelectionCallbacks() {
        disposable.clear()
        disposable.add(Trigger.watcher().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe { t ->
                when (t) {
                    is TriggerEvent.HabilitarModoMultiSelecao -> habilitarModoSelecao()
                    is TriggerEvent.DesabilitarModoMultiSelecao -> desabilitarModoSelecao()
                }
            })
    }
    
    
    fun carregarMovimentos(pesquisa: String? = null) {
        val mDAO = MovimentoContext.getDAO(mView.context)
        val itensMovimentos: List<Movimento>
        if (pesquisa.isNullOrBlank()) {
            itensMovimentos = mDAO.getTodosMovimentos()
            mView.fab_add_movimento.visibility = View.VISIBLE
        } else {
            MovimentoContext.getDAO(mView.context)
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
    
                    val section = MovimentosAdapterSection(adapter, ano, mes, itens)
                    adapter.addSection(section)
                    
                    section.setOnCheckableModeItemSelectedListener { count ->
                        atualizarQuantidadeSelecionados(count)
                    }
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
    
    private fun habilitarModoSelecao() {
        mView.fab_add_movimento.visibility = View.GONE
        multiChoiceToolbarActionMode = (requireActivity() as AppCompatActivity).startSupportActionMode(callback)
    
        val adapter = mView.rv_movimentos.adapter as SectionedRecyclerViewAdapter
        for (count in 0 until adapter.sectionCount) {
            val sec = (adapter.getSection(count) as MovimentosAdapterSection)
            sec.checkableMode = true
        }
    
        atualizarQuantidadeSelecionados(1)
    }
    
    private fun desabilitarModoSelecao() {
        val adapter = rv_movimentos.adapter as SectionedRecyclerViewAdapter
        for (count in 0 until adapter.sectionCount) {
            val sec = (adapter.getSection(count) as MovimentosAdapterSection)
            sec.checkableMode = false
        }
        
        MovimentoContext.movimentosParaExcluir.clear()
        adapter.notifyDataSetChanged()
    
        multiChoiceToolbarActionMode?.finish()
        mView.fab_add_movimento.visibility = View.VISIBLE
    }
    
    private fun atualizarQuantidadeSelecionados(count: Int) {
        multiChoiceToolbarActionMode?.title = "$count Selecionados"
    }
    
    override fun onDetach() {
        disposable.clear()
        super.onDetach()
    }
}
