package com.baltazarstudio.regular.ui.resumo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.DespesaContext
import com.baltazarstudio.regular.context.EntradaContext
import com.baltazarstudio.regular.context.MovimentoContext
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.fragment_resumo.view.*

class ResumoFragment : Fragment() {
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_resumo, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        montarMovimentos(view)
        montarDespesas(view)
        montarEntradas(view)
    }
    
    private fun montarMovimentos(view: View) {
        val dao = MovimentoContext.getDAO(view.context)
        
        val quant = dao.getQuantidadeMovimentos()
        view.tv_resumo_movimentos_quantidade_registros.text = quant.toString()
        
        val total = dao.getTotalValorMovimentos()
        view.tv_resumo_movimentos_total_gastos.text = Utils.formatCurrency(total)
        
        view.tv_resumo_movimentos_total_gastos_7_dias.text = Utils.formatCurrency(dao.getTotalValorMovimentosPorDia(7))
        view.tv_resumo_movimentos_total_gastos_30_dias.text = Utils.formatCurrency(dao.getTotalValorMovimentosPorDia(30))
        view.tv_resumo_movimentos_total_gastos_3_meses.text = Utils.formatCurrency(dao.getTotalValorMovimentosPorDia(90))
    }
    
    private fun montarDespesas(view: View) {
        val dao = DespesaContext.getDAO(view.context)
        
        val quant = dao.getQuantidadeDespesas()
        view.tv_resumo_despesas_quantidade.text = quant.toString()
        
        view.tv_resumo_despesas_soma.text = Utils.formatCurrency(dao.getValorTotalDespesas())
        view.tv_resumo_despesas_total_pago.text = Utils.formatCurrency(dao.getTotalPagoDespesas())
        
    }
    
    private fun montarEntradas(view: View) {
        val dao = EntradaContext.getDAO(view.context)
        
        val quant = dao.getQuantidadeEntradas()
        view.tv_resumo_entradas_quantidade.text = quant.toString()
        view.tv_resumo_entradas_total_entradas.text = Utils.formatCurrency(dao.getValorTotalEntradas())
        view.button_resumo_entradas_mostrar_total_entradas.setOnClickListener {
            view.tv_resumo_entradas_total_entradas.visibility = View.VISIBLE
            it.visibility = View.GONE
        }
        
        view.tv_resumo_entradas_media_6_meses.text = Utils.formatCurrency(dao.getValorMediaEntradasPorMes(6))
        view.tv_resumo_entradas_media_12_meses.text = Utils.formatCurrency(dao.getValorMediaEntradasPorMes(12))
    }
}
