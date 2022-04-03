package br.com.myself.ui.financas.entradas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.R
import br.com.myself.model.dao.EntradaDAO
import br.com.myself.model.entity.Entrada
import br.com.myself.observer.Trigger
import br.com.myself.observer.Events
import br.com.myself.ui.adapter.EntradaAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_entradas.*
import kotlinx.android.synthetic.main.fragment_entradas.view.*
import java.util.ArrayList

class EntradasFragment : Fragment() {
    
    private lateinit var mView: View
    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_entradas, container, false)
        
        return mView
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    
        val adapter = EntradaAdapter()
        mView.rv_entradas.adapter = adapter
        mView.rv_entradas.layoutManager = LinearLayoutManager(mView.context)
        
        carregarEntradas()
    
        view.button_entradas_add.setOnClickListener {
            iniciarDialogCriarEntrada()
        }
        
        registerObservables()
        
    }
    
    private fun registerObservables() {
        disposables.add(
            Trigger.watcher().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe { t ->
                    when (t) {
                        is Events.UpdateEntradas -> carregarEntradas()
                        is Events.EditarEntrada -> iniciarDialogCriarEntrada(t.entrada)
                    }
                })
    }
    
    private fun carregarEntradas() {
        val entradas = EntradaDAO(requireContext()).getTodasEntradas()
        
        mView.tv_entradas_empty.visibility =
            if (entradas.isEmpty()) View.VISIBLE else View.GONE
        (rv_entradas.adapter as EntradaAdapter).submitList(entradas)
    }
    
    private fun iniciarDialogCriarEntrada(entrada: Entrada? = null) {
        val dialog = CriarEntradaDialog(entrada)
        dialog.show(childFragmentManager, null)
    }
    
    override fun onDestroyView() {
        disposables.clear()
        super.onDestroyView()
    }
    
    class EntradaDataViewObject {
        var referencia_ano_mes: String = ""
        var entradas: ArrayList<Entrada> = arrayListOf()
    }
}