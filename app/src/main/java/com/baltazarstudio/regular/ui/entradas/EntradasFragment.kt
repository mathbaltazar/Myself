package com.baltazarstudio.regular.ui.entradas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.EntradaContext
import com.baltazarstudio.regular.model.Entrada
import com.baltazarstudio.regular.observer.Trigger
import com.baltazarstudio.regular.observer.TriggerEvent
import com.baltazarstudio.regular.ui.adapter.EntradasAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_entradas.view.*
import org.jetbrains.anko.toast

class EntradasFragment : Fragment() {
    
    private lateinit var mView: View
    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_entradas, container, false)

        setupView()

        return mView
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        disposables.add(Trigger.watcher().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { t ->
                if (t is TriggerEvent.UpdateTelaEntradas) {
                    loadEntradas()
                }
            })
    }

    private fun setupView() {
        loadEntradas()
        
        mView.button_entradas_add.setOnClickListener {
            val dialog = RegistrarEntradaDialog(mView.context)
            dialog.show()
        }
    }
    
    private fun loadEntradas() {
        val entradas = EntradaContext.getDAO(mView.context).getTodasEntradas()
        mView.tv_entradas_empty.visibility =
            if (entradas.isEmpty()) View.VISIBLE else View.GONE
    
        mView.rv_entradas.adapter = EntradasAdapter(mView.context, entradas)
        (mView.rv_entradas.adapter as EntradasAdapter).setEntradaInterface(
            object : EntradasAdapter.EntradaInterface {
                override fun onAdded(entrada: Entrada) {
                    mView.tv_entradas_empty.visibility = View.GONE
                }
                override fun onExcluded(entrada: Entrada) {
                    if (mView.rv_entradas.adapter!!.itemCount == 0)
                        mView.tv_entradas_empty.visibility = View.VISIBLE
                }
            }
        )
        mView.rv_entradas.layoutManager = LinearLayoutManager(mView.context)
        mView.rv_entradas.addItemDecoration(DividerItemDecoration(mView.context, RecyclerView.VERTICAL))
    }
    
    override fun onDetach() {
        super.onDetach()
        disposables.clear()
    }
}