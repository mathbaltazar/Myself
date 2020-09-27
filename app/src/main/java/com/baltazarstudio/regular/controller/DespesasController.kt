package com.baltazarstudio.regular.controller

import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.database.dao.DespesaDAO
import com.baltazarstudio.regular.database.dao.GastoDAO
import com.baltazarstudio.regular.model.Despesa
import com.baltazarstudio.regular.model.Gasto
import com.baltazarstudio.regular.ui.adapter.DespesasAdapter
import com.baltazarstudio.regular.ui.despesa.DespesaRegistrosDialog
import com.baltazarstudio.regular.ui.despesa.DespesasDialog
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.layout_page_movimentos_despesas.view.*
import org.jetbrains.anko.toast
import java.util.*

class DespesasController(private val view: View, private val gastosController: GastosController) {
    
    private val mDao: DespesaDAO = DespesaDAO(view.context)
    
    fun init() {
        
        view.button_despesas_info.setOnClickListener {
            view.card_despesas_info.visibility = View.VISIBLE
            view.card_despesas_info.animation = null
        }
        
        view.button_card_despesas_info_close.setOnClickListener {
            view.card_despesas_info.animation =
                AnimationUtils.loadAnimation(view.context, R.anim.fade_out)
            view.card_despesas_info.visibility = View.GONE
        }
        
        carregarDespesas()
        
        view.button_despesas_adicionar.setOnClickListener {
            val dialog = DespesasDialog(
                view.context,
                this
            )
            dialog.show()
        }
        
    }
    
    fun registrar(despesa: Despesa, data: Date, margem: Double) {
        
        val gasto = Gasto()
        gasto.descricao = despesa.nome
        gasto.valor = despesa.valor
        val date = Utils.UTCInstanceCalendar()
        date.time = data
        gasto.data = date.timeInMillis
        gasto.mes = date[Calendar.MONTH] + 1 // 0-based
        gasto.ano = date[Calendar.YEAR]
        gasto.referenciaDespesa = despesa.referencia
        gasto.margemDespesa = margem
        
        gastosController.inserir(gasto)
        view.context.toast("Registrado!")
        gastosController.carregarGastos()
        
    }
    
    fun mostrarTodosRegistros(referencia: Int) {
        val gastos = GastoDAO(view.context).getTodosGastos().filter { it.referenciaDespesa == referencia }
        val dialog =
            DespesaRegistrosDialog(
                view.context,
                gastos
            )
        dialog.show()
    }
    
    fun carregarDespesas() {
        val despesas = mDao.carregarTodasDespesas()
        
        view.rv_despesas.layoutManager = LinearLayoutManager(view.context)
        view.rv_despesas.adapter = DespesasAdapter(view.context, despesas, this)
        view.rv_despesas.addItemDecoration(
            DividerItemDecoration(
                view.context,
                RecyclerView.VERTICAL
            )
        )
    }
    
    fun inserirDespesa(despesa: Despesa) {
        mDao.inserir(despesa)
    }
}