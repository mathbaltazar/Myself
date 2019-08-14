package com.baltazarstudio.regular.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.adapter.CarteiraPendenciaAdapter
import com.baltazarstudio.regular.database.CarteiraPendenciaDAO
import com.baltazarstudio.regular.model.CarteiraPendencia
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.fragment_cateira_aberta.view.*
import java.math.BigDecimal


class CarteiraAbertaFragment : Fragment() {


    private lateinit var carteiraPendenciaDAO: CarteiraPendenciaDAO
    private lateinit var v: View

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_cateira_aberta, container, false)
        return v
    }


    private fun startView() {
        carteiraPendenciaDAO = CarteiraPendenciaDAO(context!!)

        v.btn_toggle_add_item_carteira.setOnClickListener {
            toggleNewItem()
        }

        v.btn_register_item_carteira.setOnClickListener {
            if (v.textinput_descricao.text.toString() == ""
                    || v.textinput_valor.text.toString() == "") {
                v.textinput_error.visibility = View.VISIBLE
            } else {
                v.textinput_error.visibility = View.GONE

                val item = CarteiraPendencia()
                item.descricao = v.textinput_descricao.text.toString()
                item.valor = BigDecimal(v.textinput_valor.text.toString())
                item.data = Utils.currentDateFormatted()
                carteiraPendenciaDAO.inserir(item)

                Toast.makeText(context, R.string.toast_item_carteira_adicionado, Toast.LENGTH_LONG).show()

                toggleNewItem()

                v.textinput_descricao.text = null
                v.textinput_valor.text = null

                initializeCarteiraPendencias()
            }
        }

    }

    private fun toggleNewItem() {
        if (v.layout_add_item_carteira.visibility != View.VISIBLE) {
            v.layout_add_item_carteira.visibility = View.VISIBLE
            v.divider_add_item_carteira.visibility = View.VISIBLE
            v.btn_toggle_add_item_carteira.setImageResource(android.R.drawable.ic_delete)
        } else {
            v.layout_add_item_carteira.visibility = View.GONE
            v.divider_add_item_carteira.visibility = View.GONE
            v.btn_toggle_add_item_carteira.setImageResource(android.R.drawable.ic_input_add)
            v.textinput_error.visibility = View.GONE
            Utils.hideKeyboard(context!!, v)
        }
    }

    private fun initializeCarteiraPendencias() {
        val itensCarteiraAberta = carteiraPendenciaDAO.getTodos()

        v.listview_carteira_aberta.adapter = CarteiraPendenciaAdapter(this, itensCarteiraAberta)
        if (itensCarteiraAberta.size == 0) {
            v.tv_sem_pendencias.visibility = View.VISIBLE
        } else {
            v.tv_sem_pendencias.visibility = View.GONE
        }
    }

    fun createDialogExcluir(item: CarteiraPendencia): Boolean {
        AlertDialog.Builder(context!!)
                .setTitle(R.string.all_dialog_title_excluir)
                .setMessage(R.string.all_dialog_message_excluir)
                .setPositiveButton(R.string.all_string_sim, object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        carteiraPendenciaDAO.excluir(item)
                        Toast.makeText(context, R.string.all_toast_carteira_pendencia_removida, Toast.LENGTH_SHORT).show()

                        initializeCarteiraPendencias()
                    }
                })
                .setNegativeButton(R.string.all_string_nao, null)
                .show()
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        startView()
        initializeCarteiraPendencias()
    }

}
