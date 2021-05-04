package com.baltazarstudio.regular.ui.movimentacao.despesa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.context.DespesaContext
import com.baltazarstudio.regular.ui.adapter.DespesasAdapter
import kotlinx.android.synthetic.main.fragment_despesas.view.*

class DespesasFragment : Fragment() {
    
    private lateinit var mView: View
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
    
        carregarDespesas()
    
        mView.button_despesas_adicionar.setOnClickListener {
            val dialog = CriarDespesaDialog(mView.context)
            dialog.show()
        }
    }
    
    fun carregarDespesas() {
        val despesas = DespesaContext.getDAO(mView.context).getTodasDespesas()
        
        if (despesas.isEmpty()) {
            mView.tv_despesas_sem_depesas.visibility = View.VISIBLE
        } else {
            mView.tv_despesas_sem_depesas.visibility = View.GONE
        }
        
        mView.rv_despesas.layoutManager = LinearLayoutManager(mView.context)
        mView.rv_despesas.adapter = DespesasAdapter(mView.context, despesas)
        mView.rv_despesas.addItemDecoration(
            DividerItemDecoration(mView.context, RecyclerView.VERTICAL))
    }
    
}
