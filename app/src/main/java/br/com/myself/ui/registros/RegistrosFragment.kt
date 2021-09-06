package br.com.myself.ui.registros

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.com.myself.R
import br.com.myself.context.RegistroContext
import br.com.myself.model.Registro
import br.com.myself.observer.Trigger
import br.com.myself.observer.Events
import br.com.myself.ui.adapter.RegistrosAdapterSection
import br.com.myself.util.Utils
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_registros.view.*

class RegistrosFragment : Fragment() {
    
    private lateinit var mView: View
    private var multiChoiceToolbarActionMode: androidx.appcompat.view.ActionMode? = null
    private var callback: androidx.appcompat.view.ActionMode.Callback? = null
    
    private val disposable = CompositeDisposable()
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_registros, container, false)
        return mView
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerCallbacks()
        
        carregarRegistros()
    }
    
    private fun registerCallbacks() {
        disposable.clear()
        disposable.add(
            Trigger.watcher().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe { t ->
                when (t) {
                    is Events.UpdateRegistros -> carregarRegistros()
                    is Events.FiltrarRegistrosPelaDescricao -> carregarRegistros()
                    is Events.HabilitarModoMultiSelecao -> habilitarModoSelecao()
                }
            })
    }
    
    private fun carregarRegistros() {
        val itensRegistros =
            RegistroContext.run { getDAO(mView.context).getTodosRegistros(textoPesquisa) }
        
        if (itensRegistros.isNotEmpty()) {
            mView.tv_registros_sem_registros.visibility = View.GONE
            mView.rv_registros.visibility = View.VISIBLE
            
            val adapter = SectionedRecyclerViewAdapter()
            
            for (ano in Utils.getAnosDisponiveis(itensRegistros)) {
                for (mes in Utils.getMesDisponivelPorAno(itensRegistros, ano)) {
                    val itens = Utils.filtrarItensPorData(itensRegistros, mes, ano)
                    
                    if (itens.isEmpty()) continue
                    
                    val section =
                        RegistrosAdapterSection(
                            adapter,
                            ano,
                            mes,
                            itens as List<Registro>
                        )
                    adapter.addSection(section)
                    
                    section.setOnCheckableModeItemSelectedListener { count ->
                        atualizarQuantidadeSelecionados(count)
                    }
                }
            }
            
            mView.rv_registros.adapter = adapter
            mView.rv_registros.layoutManager = LinearLayoutManager(mView.context)
            
        } else {
            mView.tv_registros_sem_registros.visibility = View.VISIBLE
            mView.rv_registros.visibility = View.GONE
        }
        
    }
    
    private fun habilitarModoSelecao() {
        multiChoiceToolbarActionMode =
            (requireActivity() as AppCompatActivity).startSupportActionMode(getCallback())
        
        val adapter = mView.rv_registros.adapter as SectionedRecyclerViewAdapter
        for (count in 0 until adapter.sectionCount) {
            val sec = (adapter.getSection(count) as RegistrosAdapterSection)
            sec.checkableMode = true
        }
        
        atualizarQuantidadeSelecionados(1)
    }
    
    private fun getCallback(): ActionMode.Callback {
        if (callback == null) {
            callback = object : androidx.appcompat.view.ActionMode.Callback {
                override fun onCreateActionMode(
                    mode: androidx.appcompat.view.ActionMode?, menu: Menu?
                ): Boolean {
                    requireActivity().menuInflater.inflate(
                        R.menu.menu_action_mode_multi_selecao,
                        menu
                    )
                    return true
                }
                
                override fun onPrepareActionMode(
                    mode: androidx.appcompat.view.ActionMode?, menu: Menu?
                ): Boolean {
                    return false
                }
                
                override fun onActionItemClicked(
                    mode: androidx.appcompat.view.ActionMode?, item: MenuItem?
                ): Boolean {
                    when (item?.itemId) {
                        R.id.action_delete_movimentos -> {
                            if (RegistroContext.registrosParaExcluir.size == 0) {
                                Trigger.launch(
                                    Events.Toast("Selecione pelo menos 1 registro"))
                                mode?.subtitle = "Selecione pelo menos 1 registro"
                            } else {
                                AlertDialog.Builder(mView.context).setTitle("Excluir")
                                    .setMessage("Confirma a exclusão dos itens selecionados?")
                                    .setPositiveButton("Excluir") { _, _ ->
                                        val countExcluidos =
                                            RegistroContext.excluirMovimentosSelecionados(mView.context)
                                        
                                        Trigger.launch(
                                            Events.Snack("$countExcluidos Registros Removidos"),
                                            Events.UpdateRegistros(),
                                            Events.UpdateDespesas()
                                        )
                                        
                                        mode?.finish()
                                    }.setNegativeButton("Cancelar", null).show()
                            }
                        }
                    }
                    return true
                }
                
                override fun onDestroyActionMode(mode: androidx.appcompat.view.ActionMode?) {
                    val adapter = mView.rv_registros.adapter as SectionedRecyclerViewAdapter
                    for (count in 0 until adapter.sectionCount) {
                        val sec = (adapter.getSection(count) as RegistrosAdapterSection)
                        sec.checkableMode = false
                    }
                    
                    RegistroContext.registrosParaExcluir.clear()
                    adapter.notifyDataSetChanged()
                    mode?.subtitle = null
                    Trigger.launch(Events.DesabilitarModoMultiSelecao())
                }
            }
        }
        
        return callback!!
    }
    
    private fun atualizarQuantidadeSelecionados(count: Int) {
        multiChoiceToolbarActionMode?.title = "$count Selecionados"
    }
    
    //Utilizado por causa do childFragmentManager - ideal seria onDetach()
    override fun onDestroyView() {
        disposable.clear()
        super.onDestroyView()
    }
}
