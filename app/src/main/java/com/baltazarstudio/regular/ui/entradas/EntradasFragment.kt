package com.baltazarstudio.regular.ui.entradas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.EntradaContext
import com.baltazarstudio.regular.model.Entrada
import com.baltazarstudio.regular.observer.Trigger
import com.baltazarstudio.regular.observer.Events
import com.baltazarstudio.regular.ui.adapter.EntradasAdapterSection
import com.baltazarstudio.regular.util.Utils
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_entradas.view.*

class EntradasFragment : Fragment() {
    
    private lateinit var mView: View
    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_entradas, container, false)

        setupView()

        return mView
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        disposables.add(Trigger.watcher().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { t ->
                if (t is Events.UpdateEntradas) {
                    carregarEntradas()
                }
            })
    }

    private fun setupView() {
        carregarEntradas()
        
        mView.button_entradas_add.setOnClickListener {
            val dialog = CriarEntradaDialog(mView.context)
            dialog.show()
        }
    }
    
    private fun carregarEntradas() {
        val entradas = EntradaContext.getDAO(mView.context).getTodasEntradas()
        mView.tv_entradas_empty.visibility =
            if (entradas.isEmpty()) View.VISIBLE else View.GONE
        
        val adapter = SectionedRecyclerViewAdapter()
        
        for (ano in Utils.getAnosDisponiveis(entradas)) {
            for (mes in Utils.getMesDisponivelPorAno(entradas, ano)) {
                val itens = Utils.filtrarItensPorData(entradas, mes, ano)
                
                if (itens.isEmpty()) continue
                
                val section = EntradasAdapterSection(adapter, itens as List<Entrada>, mes, ano)
                adapter.addSection(section)
            }
        }
    
        mView.rv_entradas.adapter = adapter
        mView.rv_entradas.layoutManager = LinearLayoutManager(mView.context)
    }
    
    override fun onDetach() {
        super.onDetach()
        disposables.clear()
    }
}