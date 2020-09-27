package com.baltazarstudio.regular.ui.movimentos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.controller.DespesasController
import com.baltazarstudio.regular.controller.GastosController
import com.baltazarstudio.regular.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_movimentos.*
import java.lang.IndexOutOfBoundsException


class MovimentosFragment : Fragment() {
    
    private lateinit var gastoController: GastosController
    private lateinit var despesasController: DespesasController
    private var firstUse: Boolean = true
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_movimentos, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }
    
    private fun setUpView() {
        
        vp_movimentos.adapter = object : PagerAdapter() {
            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view == `object`
            }
            
            override fun getCount(): Int {
                return 2
            }
            
            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                return initializeControllers(container, position)
            }
            
            override fun getPageTitle(position: Int): CharSequence? {
                return if (position == 0) "Gastos" else "Despesas"
            }
            
        }
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
    
    private fun initializeControllers(container: ViewGroup, position: Int): View {
        var view: View? = null
        if (position == 0) {
            view = layoutInflater.inflate(R.layout.layout_page_movimentos_gastos, container, false)
            gastoController = GastosController(view)
            gastoController.init()
        } else if (position == 1) {
            view = layoutInflater.inflate(R.layout.layout_page_movimentos_despesas, container, false)
            despesasController = DespesasController(view, gastoController)
            despesasController.init()
        }
        
        if (view != null) {
            container.addView(view)
            return view
        }
        throw IndexOutOfBoundsException("MovimentosFragment#initializeControllers: There are no view left")
    }
    
    fun filtrarDescricao(query: String?) {
        gastoController.carregarGastos(query)
    }
    
    override fun onResume() {
        super.onResume()
        
        if (activity?.intent?.action == "abrir_adicionar_gasto" && firstUse) {
            gastoController.adicionarGasto()
            (requireActivity() as MainActivity).searchMenuItem?.collapseActionView()
        }
        firstUse = false
    }
    
}
