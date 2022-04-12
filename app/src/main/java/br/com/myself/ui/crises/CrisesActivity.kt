package br.com.myself.ui.crises

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.R
import br.com.myself.domain.entity.Crise
import br.com.myself.domain.repository.CriseRepository
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.ui.adapter.CrisesAdapter
import br.com.myself.util.Async
import br.com.myself.util.Utils.Companion.formattedDate
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_crises.*
import org.jetbrains.anko.toast
import java.util.*

class CrisesActivity : AppCompatActivity() {
    
    private val repository: CriseRepository by lazy { CriseRepository(applicationContext) }
    private val disposable: CompositeDisposable = CompositeDisposable()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crises)
        
        setUpView()
        configureAdapter()
        loadCrises()
        
        registerObservables()
    }
    
    @SuppressLint("SetTextI18n")
    private fun setUpView() {
        button_crises_mais_detalhes.setOnClickListener {
            // TOGGLE LAYOUT MAIS DETALHES
            if (ll_crises_mais_detalhes.visibility != View.VISIBLE) {
                ll_crises_mais_detalhes.visibility = View.VISIBLE
                button_crises_mais_detalhes.setIconResource(R.drawable.ic_arrow_up)
                button_crises_mais_detalhes.text = "Menos"
            } else {
                ll_crises_mais_detalhes.visibility = View.GONE
                button_crises_mais_detalhes.setIconResource(R.drawable.ic_arrow_down)
                button_crises_mais_detalhes.text = "Mais"
            }
        }
    
        button_crises_registrar_crise.setOnClickListener {
            val dialog = RegistrarCriseDialog(repository = repository)
            dialog.show(supportFragmentManager, null)
        }
        
    }
    
    private fun configureAdapter() {
        recycler_view_crises.adapter = CrisesAdapter().apply {
            setOnItemClickListener { crise, view ->
                showPopupMenu(crise, view)
            }
        }
        
        recycler_view_crises.layoutManager = LinearLayoutManager(this)
    }
    
    private fun showPopupMenu(crise: Crise, view: View) {
        val popup = PopupMenu(this@CrisesActivity, view, Gravity.END)/*.also { inflateLongClickMenu(it, crise) }*/
        popup.menu.add("Editar").setOnMenuItemClickListener {
            val dialog = RegistrarCriseDialog(crise, repository)
            dialog.show(supportFragmentManager, null)
            
            true
        }
    
        popup.menu.add("Excluir").setOnMenuItemClickListener {
            excluirCrise(crise)
            true
        }
        popup.show()
    }
    
    private fun excluirCrise(crise: Crise) {
        var mensagem = "Data: ${crise.data.formattedDate()}"
        mensagem += "\nHorários: Entre ${crise.horario1} e ${crise.horario2}"
        mensagem += "\nObservações: ${crise.observacoes}"
    
        AlertDialog.Builder(this).setTitle("Excluir")
            .setMessage(mensagem)
            .setPositiveButton("Excluir") { _, _ ->
                
                Async.doInBackground({ repository.excluir(crise) }, {
                    toast("Removido!")
                    Trigger.launch(Events.UpdateCrises)
                })
                
            }.setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun loadCrises() {
        Async.doInBackground({ repository.getTodasCrises() }, { crises ->
            
            (recycler_view_crises.adapter as CrisesAdapter).submitList(crises)
        
            tv_crises_sem_crises_registradas.visibility =
                if (crises.isEmpty()) View.VISIBLE else View.GONE
    
            tv_crises_numero_crises.text = crises.size.toString()
            
        })
    }
    
    private fun registerObservables() {
        disposable.clear()
        disposable.add(Trigger.watcher().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { t ->
                when (t) {
                    is Events.UpdateCrises -> loadCrises()
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
