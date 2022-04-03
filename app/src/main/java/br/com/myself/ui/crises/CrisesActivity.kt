package br.com.myself.ui.crises

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.R
import br.com.myself.context.CriseContext
import br.com.myself.model.entity.Crise
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.ui.adapter.CrisesAdapter
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_crises.*
import java.util.*

class CrisesActivity : AppCompatActivity() {
    
    private var detalhesExpanded: Boolean = false
    private val disposable: CompositeDisposable = CompositeDisposable()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crises)
    
        CriseContext.obterCrises(this)
        
        setUpView()
        
        registrarObservables()
        
    }
    
    private fun setUpView() {
        
        button_crises_mais_detalhes.setOnClickListener {
            detalhesExpanded = !detalhesExpanded
            
            if (detalhesExpanded) {
                ll_crises_mais_detalhes.visibility = View.VISIBLE
                button_crises_mais_detalhes.setIconResource(R.drawable.ic_arrow_up)
            } else {
                ll_crises_mais_detalhes.visibility = View.GONE
                button_crises_mais_detalhes.setIconResource(R.drawable.ic_arrow_down)
            }
        }
    
        button_crises_registrar_crise.setOnClickListener {
            val dialog = RegistrarCriseDialog()
            dialog.show(supportFragmentManager, null)
        }
        
    
        recycler_view_crises.layoutManager = LinearLayoutManager(this)
        recycler_view_crises.adapter = CrisesAdapter(this) {
            RegistrarCriseDialog(it).show(supportFragmentManager, null)
        }
    
        dropdown_crises_filtro_anos.setAdapter(montarAdapterFiltroAnosCrises())
        dropdown_crises_filtro_anos.setOnItemClickListener { _, _, position, _ ->
            CriseContext.criseDataView.filtroAnos = position
            CriseContext.filtrarCrises()
            refreshCrises()
        }
    
        refreshCrises(false)
    }
    
    private fun montarAdapterFiltroAnosCrises(): ArrayAdapter<String> {
        val adapter = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_dropdown_item,
            Arrays.asList("Todos", "1 ano", "2 anos")
        )
        return adapter
    }
    
    private fun refreshCrises(updateDataSet: Boolean = true) {
        if (updateDataSet) recycler_view_crises.adapter?.notifyDataSetChanged()
        
        if (CriseContext.criseDataView.crises.isEmpty()) {
            tv_crises_sem_crises_registradas.visibility = View.VISIBLE
        } else {
            tv_crises_sem_crises_registradas.visibility = View.GONE
        }
    
        tv_crises_numero_crises.setText(CriseContext.criseDataView.crises.size.toString())
    }
    
    private fun registrarObservables() {
        disposable.clear()
        disposable.add(Trigger.watcher().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { t ->
                when (t) {
                    is Events.UpdateCrises -> refreshCrises()
                }
            })
    }
    
    
    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }
    
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
    
    class CriseDataViewObject {
        var crises: ArrayList<Crise> = arrayListOf()
        var filtroAnos: Int = 0
    }
}
