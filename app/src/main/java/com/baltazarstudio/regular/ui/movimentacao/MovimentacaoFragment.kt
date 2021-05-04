package com.baltazarstudio.regular.ui.movimentacao

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.observer.Trigger
import com.baltazarstudio.regular.observer.Events
import com.baltazarstudio.regular.ui.MainActivity
import com.baltazarstudio.regular.ui.movimentacao.despesa.CriarDespesaDialog
import com.baltazarstudio.regular.ui.movimentacao.despesa.DespesasFragment
import com.baltazarstudio.regular.ui.movimentacao.registros.RegistrosFragment
import com.baltazarstudio.regular.ui.movimentacao.registros.CriarRegistroDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_movimentacao.*


class MovimentacaoFragment : Fragment() {
    
    private var firstUse: Boolean = true
    private val registrosFragment = RegistrosFragment()
    private val despesasFragment = DespesasFragment()
    
    private val disposables = CompositeDisposable()
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_movimentacao, container, false)
    }
    
    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        registerCallbacks()
    
        val activity = (requireActivity() as MainActivity)
    
        bottom_navigation_view_movimentacao.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_navigation_registros -> {
                    childFragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container_movimentacao, registrosFragment)
                        .addToBackStack(null)
                        .commit()
                
                    activity.searchMenuItem?.isVisible = true
                    activity.toolbar.title = "Meus Registros"
                
                    true
                }
                R.id.bottom_navigation_despesas -> {
                    childFragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container_movimentacao, despesasFragment)
                        .addToBackStack(null)
                        .commit()
    
                    val activity = (requireActivity() as MainActivity)
                    activity.searchMenuItem?.isVisible = false
                    activity.toolbar.title = "Minhas Despesas"
                    
                    true
                }
                else -> false
            }
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
    
    private fun setUpView() {
        /*val adapter = FragmentTabAdapter(childFragmentManager)
        adapter.addFragment(registrosFragment, "Registros")
        adapter.addFragment(despesasFragment, "Despesas")
        vp_movimentos.adapter = adapter
        
        tablayout_movimentos.setupWithViewPager(vp_movimentos)
        tablayout_movimentos.getTabAt(0)!!.setIcon(R.drawable.ic_registros)
        tablayout_movimentos.getTabAt(1)!!.setIcon(R.drawable.ic_despesas)
        
        vp_movimentos.addOnPageChangeListener(object :
        ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                (requireActivity() as MainActivity).searchMenuItem?.isVisible = position == 0
            }
        })*/
        
    }
    
    private fun registerCallbacks() {
        disposables.clear()
        disposables.add(Trigger.watcher().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { t ->
                when (t) {
                    is Events.UpdateRegistros -> registrosFragment.carregarMovimentos()
                    is Events.UpdateDespesas -> despesasFragment.carregarDespesas()
                    is Events.FiltrarMovimentosPelaDescricao -> registrosFragment.carregarMovimentos()
                    is Events.HabilitarModoMultiSelecao -> bottom_navigation_view_movimentacao.visibility = View.GONE //desabilitarTabs()
                    is Events.DesabilitarModoMultiSelecao -> bottom_navigation_view_movimentacao.visibility = View.VISIBLE //habilitarTabs()
                }
            })
    }
    
    fun habilitarTabs() {
        //tablayout_movimentos.visibility = View.VISIBLE
        //vp_movimentos.locked = false
    }
    
    fun desabilitarTabs() {
        //tablayout_movimentos.visibility = View.GONE
        //vp_movimentos.locked = true
    }
    
    override fun onResume() {
        super.onResume()
        
        if (activity?.intent?.action == "abrir_adicionar_gasto" && firstUse) {
            val dialog = CriarRegistroDialog(context!!)
            dialog.show()
        }
        firstUse = false
    }
    
}
