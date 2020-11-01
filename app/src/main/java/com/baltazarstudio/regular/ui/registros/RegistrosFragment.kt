package com.baltazarstudio.regular.ui.registros

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.observer.Trigger
import com.baltazarstudio.regular.observer.TriggerEvent
import com.baltazarstudio.regular.ui.MainActivity
import com.baltazarstudio.regular.ui.adapter.FragmentTabAdapter
import com.baltazarstudio.regular.ui.registros.despesa.DespesasFragment
import com.baltazarstudio.regular.ui.registros.movimentos.MovimentosFragment
import com.baltazarstudio.regular.ui.registros.movimentos.RegistrarMovimentoDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_registros.*


class RegistrosFragment : Fragment() {
    
    private var firstUse: Boolean = true
    private val movimentosFragment = MovimentosFragment()
    private val despesasFragment = DespesasFragment()
    
    private val disposables = CompositeDisposable()
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_registros, container, false)
    }
    
    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        
        disposables.clear()
        disposables.add(Trigger.watcher().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { t ->
                when (t) {
                    is TriggerEvent.UpdateTelaMovimento -> movimentosFragment.carregarMovimentos()
                    is TriggerEvent.UpdateTelaDespesa -> despesasFragment.carregarDespesas()
                    is TriggerEvent.FiltrarMovimentosPelaDescricao -> movimentosFragment.carregarMovimentos(t.newText)
                    is TriggerEvent.PrepareMultiChoiceRegistrosLayout -> prepareMultiSelectLayout(t.show)
                }
        })
    }
    
    private fun setUpView() {
        val adapter = FragmentTabAdapter(childFragmentManager)
        adapter.addFragment(movimentosFragment, "Gastos")
        adapter.addFragment(despesasFragment, "Despesas")
        vp_movimentos.adapter = adapter
        
        tablayout_movimentos.setupWithViewPager(vp_movimentos)
        tablayout_movimentos.getTabAt(0)!!.setIcon(R.drawable.ic_gastos)
        tablayout_movimentos.getTabAt(1)!!.setIcon(R.drawable.ic_despesas)
        
        vp_movimentos.addOnPageChangeListener(object :
        ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                (requireActivity() as MainActivity).searchMenuItem?.isVisible = position == 0
            }
        })
        
    }
    
    private fun prepareMultiSelectLayout(showLayout: Boolean) {
        if (showLayout) {
            tablayout_movimentos.visibility = View.VISIBLE
        } else {
            tablayout_movimentos.visibility = View.GONE
        }
    
        (requireActivity() as MainActivity).searchMenuItem?.isVisible = showLayout
        vp_movimentos.locked = !showLayout
    }
    
    override fun onResume() {
        super.onResume()
        
        if (activity?.intent?.action == "abrir_adicionar_gasto" && firstUse) {
            val dialog = RegistrarMovimentoDialog(context!!)
            dialog.show()
        }
        firstUse = false
    }
    
}
