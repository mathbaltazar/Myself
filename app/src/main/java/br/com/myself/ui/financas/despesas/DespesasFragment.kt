package br.com.myself.ui.financas.despesa

import android.app.ActivityOptions
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.R
import br.com.myself.context.DespesaContext
import br.com.myself.model.Despesa
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.ui.adapter.DespesasAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_despesas.view.*
import org.jetbrains.anko.support.v4.intentFor

class DespesasFragment : Fragment() {
    
    private val disposables: CompositeDisposable = CompositeDisposable()
    private lateinit var mView: View
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_despesas, container, false)
        return mView
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        mView.button_despesas_info.setOnClickListener {
            mView.card_despesas_info.visibility = View.VISIBLE
            mView.card_despesas_info.animation = null
        }
        
        mView.button_card_despesas_info_close.setOnClickListener {
            mView.card_despesas_info.animation =
                AnimationUtils.loadAnimation(mView.context, R.anim.fade_out)
            mView.card_despesas_info.visibility = View.GONE
        }
        
        mView.fab_despesas_adicionar.setOnClickListener {
            CriarDespesaDialog(it.context).show()
        }
    
        mView.rv_despesas.layoutManager = LinearLayoutManager(mView.context)
        mView.rv_despesas.adapter = DespesasAdapter(mView.context)
        
        DespesaContext.obterDespesas(mView.context)
        
        carregarDespesas()
        
        registrarObservables()
        
    }
    
    private fun carregarDespesas() {
        val despesas = DespesaContext.despesasDataView.despesas
        
        if (despesas.isEmpty()) {
            mView.tv_despesas_sem_depesas.visibility = View.VISIBLE
            mView.rv_despesas.visibility = View.GONE
        } else {
            mView.tv_despesas_sem_depesas.visibility = View.GONE
            mView.rv_despesas.visibility = View.VISIBLE
        }
        
        mView.rv_despesas.adapter?.notifyDataSetChanged()
    }
    
    private fun registrarObservables() {
        disposables.clear()
        disposables.add(Trigger.watcher().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe { t ->
                    when (t) {
                        is Events.UpdateDespesas -> carregarDespesas()
                    }
                })
    }
    
    class DespesaDataViewObject {
        var despesas: ArrayList<Despesa> = arrayListOf()
        var despesaDetalhada: Despesa? = null
    }
}
