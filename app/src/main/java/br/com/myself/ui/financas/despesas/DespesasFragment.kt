package br.com.myself.ui.financas.despesas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.R
import br.com.myself.domain.repository.DespesaRepository
import br.com.myself.domain.repository.RegistroRepository
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.ui.adapter.DespesaAdapter
import br.com.myself.util.Async
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_despesas.*

class DespesasFragment(
    private val despesaRepository: DespesaRepository,
    private val registroRepository: RegistroRepository
) : Fragment() {
    
    private val disposables: CompositeDisposable = CompositeDisposable()
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View  = inflater.inflate(R.layout.fragment_despesas, container, false)
        
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        button_despesas_info.setOnClickListener {
            card_despesas_info.visibility = View.VISIBLE
            card_despesas_info.animation = null
        }
        
        button_card_despesas_info_close.setOnClickListener {
            card_despesas_info.animation =
                AnimationUtils.loadAnimation(it.context, R.anim.fade_out)
            card_despesas_info.visibility = View.GONE
        }
        
        fab_despesas_adicionar.setOnClickListener {
            CriarDespesaDialog(it.context, despesaRepository).show()
        }
    
        recyclerview_despesas.layoutManager = LinearLayoutManager(requireContext())
        recyclerview_despesas.adapter = DespesaAdapter(despesaRepository)
        
        carregarDespesas()
        
        registrarObservables()
    }
    
    private fun carregarDespesas() {
        Async.doInBackground({ despesaRepository.getAllDespesas() }, { despesas ->
    
            tv_despesas_sem_depesas.visibility =
                if (despesas.isEmpty()) View.VISIBLE else View.GONE
    
            (recyclerview_despesas.adapter as DespesaAdapter).submitList(despesas)
        })
    }
    
    private fun registrarObservables() {
        disposables.clear()
        disposables.add(Trigger.watcher().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe { t ->
                    when (t) {
                        is Events.UpdateDespesas -> carregarDespesas()
                        is Events.RegistrarDespesa -> {
                            val dialog = RegistrarDespesaDialog(t.despesa, registroRepository)
                            dialog.show(childFragmentManager, null)
                        }
                    }
                })
    }
    
}
