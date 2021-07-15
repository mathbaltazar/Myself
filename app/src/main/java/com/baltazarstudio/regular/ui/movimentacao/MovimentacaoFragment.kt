package com.baltazarstudio.regular.ui.movimentacao

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.observer.Trigger
import com.baltazarstudio.regular.observer.Events
import com.baltazarstudio.regular.ui.MainActivity
import com.baltazarstudio.regular.ui.despesa.CriarDespesaDialog
import com.baltazarstudio.regular.ui.despesa.DespesasFragment
import com.baltazarstudio.regular.ui.registros.RegistrosFragment
import com.baltazarstudio.regular.ui.registros.CriarRegistroDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_movimentacao.*


class MovimentacaoFragment : Fragment() {
    
    private var firstUse: Boolean = true
    
    private val disposables = CompositeDisposable()
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_movimentacao, container, false)
    }
    
    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerCallbacks()
    
        val activity = (requireActivity() as MainActivity)
    
        bottom_navigation_view_movimentacao.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_navigation_registros -> {
                    childFragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container_movimentacao, RegistrosFragment())
                        .commit()
                    
                    activity.toolbar.title = "Meus Registros"
                }
                R.id.bottom_navigation_despesas -> {
                    childFragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container_movimentacao, DespesasFragment())
                        .commit()
    
                    activity.toolbar.title = "Minhas Despesas"
                }
            }
    
            activity.searchMenuItem?.isVisible = item.itemId == R.id.bottom_navigation_registros
            activity.archiveMenuItem?.isVisible = item.itemId == R.id.bottom_navigation_despesas
            true
        }
        
        view.post {
            activity.toolbar.menu.findItem(R.id.action_adicionar).setOnMenuItemClickListener {
                when (bottom_navigation_view_movimentacao.selectedItemId) {
                    R.id.bottom_navigation_registros -> CriarRegistroDialog(view.context).show()
                    R.id.bottom_navigation_despesas -> CriarDespesaDialog(view.context).show()
                }
                true
            }
        }
        
        if (savedInstanceState == null)
            // Inicializar o frgament
            bottom_navigation_view_movimentacao.selectedItemId = R.id.bottom_navigation_registros
    }
    
    private fun registerCallbacks() {
        disposables.clear()
        disposables.add(Trigger.watcher().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { t ->
                when (t) {
                    is Events.HabilitarModoMultiSelecao -> bottom_navigation_view_movimentacao.visibility = View.GONE //desabilitarTabs()
                    is Events.DesabilitarModoMultiSelecao -> bottom_navigation_view_movimentacao.visibility = View.VISIBLE //habilitarTabs()
                }
            })
    }
    
    override fun onResume() {
        super.onResume()
        
        view?.post {
            if (activity?.intent?.action == "abrir_adicionar_gasto" && firstUse) {
                val dialog = CriarRegistroDialog(context!!)
                dialog.show()
            }
            firstUse = false
        }
    }
    
    override fun onDetach() {
        disposables.clear()
        super.onDetach()
    }
}
