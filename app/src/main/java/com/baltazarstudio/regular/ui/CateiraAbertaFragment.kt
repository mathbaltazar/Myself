package com.baltazarstudio.regular.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.adapter.ItemCarteiraAdapter
import com.baltazarstudio.regular.database.ItemCarteiraAbertaDAO
import com.baltazarstudio.regular.model.ItemCarteiraAberta
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.fragment_cateira_aberta.*
import kotlinx.android.synthetic.main.fragment_cateira_aberta.view.*
import java.math.BigDecimal


class CateiraAbertaFragment(var activity: Activity) : Fragment() {


    private lateinit var carteiraAbertaDAO: ItemCarteiraAbertaDAO
    private lateinit var v: View

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_cateira_aberta, container, false)
        return v
    }


    private fun init() {
        carteiraAbertaDAO = ItemCarteiraAbertaDAO(context!!)

        v.btn_toggle_add_item_carteira.setOnClickListener {
            if (v.layout_add_item_carteira.visibility != View.VISIBLE) {
                v.layout_add_item_carteira.visibility = View.VISIBLE
                v.divider_add_item_carteira.visibility = View.VISIBLE
                v.btn_toggle_add_item_carteira.setImageResource(android.R.drawable.ic_delete)
            } else {
                v.layout_add_item_carteira.visibility = View.GONE
                v.divider_add_item_carteira.visibility = View.GONE
                v.btn_toggle_add_item_carteira.setImageResource(android.R.drawable.ic_input_add)
                Utils.hideKeyboard(context!!, v)
            }
        }

        v.btn_register_item_carteira.setOnClickListener {
            if (v.textinput_descricao.text.toString() == ""
                    || v.textinput_descricao.text.toString() == "") {
                textinput_error.visibility = View.VISIBLE
            } else {
                textinput_error.visibility = View.GONE

                val item = ItemCarteiraAberta()
                item.descricao = v.textinput_descricao.text.toString()
                item.valor = BigDecimal(v.textinput_valor.text.toString())
                item.data = Utils.currentDateFormatted()
                carteiraAbertaDAO.inserir(item)

                Toast.makeText(context, R.string.toast_item_carteira_adicionado, Toast.LENGTH_LONG).show()

                v.btn_toggle_add_item_carteira.performClick()

                v.textinput_descricao.text = null
                v.textinput_valor.text = null

                getItensCarteira()
            }
        }
    }

    fun getItensCarteira() {
        val itensCarteiraAberta = carteiraAbertaDAO.getTodos()

        if (itensCarteiraAberta.size == 0) {
            v.tv_sem_pendencias.visibility = View.VISIBLE
        } else {
            v.tv_sem_pendencias.visibility = View.GONE
            v.list_carteira_aberta.adapter = ItemCarteiraAdapter(activity, itensCarteiraAberta)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
        getItensCarteira()
    }

}
