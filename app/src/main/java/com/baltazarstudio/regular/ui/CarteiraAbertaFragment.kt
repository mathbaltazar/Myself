package com.baltazarstudio.regular.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.adapter.CarteiraPendenciaAdapter
import com.baltazarstudio.regular.database.dao.CarteiraPendenciaDAO
import com.baltazarstudio.regular.model.CarteiraPendencia
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.dialog_add_element.view.*
import kotlinx.android.synthetic.main.fragment_carteira_aberta.view.*
import java.math.BigDecimal


class CarteiraAbertaFragment : Fragment() {


    private lateinit var carteiraPendenciaDAO: CarteiraPendenciaDAO
    private lateinit var v: View

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_carteira_aberta, container, false)
        return v
    }


    private fun startView() {
        carteiraPendenciaDAO = CarteiraPendenciaDAO(context!!)

        v.button_add_pendencia.setOnClickListener {
            createDialogNovaPendencia()
        }

        refreshListPendencias()
    }


    private fun refreshListPendencias() {
        val listItensPendencias = carteiraPendenciaDAO.getTodos()
        v.listview_carteira_pendencias.adapter = CarteiraPendenciaAdapter(this, listItensPendencias)

        if (listItensPendencias.isEmpty()) {
            v.tv_sem_pendencias.visibility = View.VISIBLE
        } else {
            v.tv_sem_pendencias.visibility = View.GONE
        }
    }

    @SuppressLint("InflateParams")
    private fun createDialogNovaPendencia() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_element, null)
        val dialog = AlertDialog.Builder(context!!)
                .setView(dialogView)
                .create()

        dialogView.dialog_add_element_button_adicionar.setOnClickListener {
            if (dialogView.textinput_descricao.text.toString() == ""
                    || dialogView.textinput_valor.text.toString() == ""
            ) {
                dialogView.textinput_error.visibility = View.VISIBLE
            } else {
                addPendencia(
                        dialogView.textinput_descricao.text.toString(),
                        dialogView.textinput_valor.text.toString()
                )
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun addPendencia(descricao: String, valor: String) {
        val item = CarteiraPendencia()
        item.descricao = descricao
        item.valor = BigDecimal(valor)
        item.data = Utils.currentDateFormatted()
        carteiraPendenciaDAO.inserir(item)

        Toast.makeText(context, R.string.toast_carteira_pendencia_adicionada, Toast.LENGTH_LONG).show()

        refreshListPendencias()
    }

    fun createDialogExcluir(item: CarteiraPendencia): Boolean {
        AlertDialog.Builder(context!!)
                .setTitle(R.string.all_dialog_title_excluir)
                .setMessage(R.string.all_dialog_message_excluir)
                .setPositiveButton(R.string.all_string_sim) { _, _ ->
                    carteiraPendenciaDAO.excluir(item)
                    Toast.makeText(context, R.string.toast_carteira_pendencia_removida, Toast.LENGTH_SHORT).show()

                    refreshListPendencias()
                }
                .setNegativeButton(R.string.all_string_nao, null)
                .show()
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        startView()
    }

}
