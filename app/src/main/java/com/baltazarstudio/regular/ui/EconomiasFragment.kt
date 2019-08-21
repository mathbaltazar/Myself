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
import com.baltazarstudio.regular.adapter.EconomiasAdapter
import com.baltazarstudio.regular.database.dao.EconomiaDAO
import com.baltazarstudio.regular.model.Economia
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.dialog_add_element.view.*
import kotlinx.android.synthetic.main.fragment_economias.view.*
import java.math.BigDecimal

class EconomiasFragment : Fragment() {

    private lateinit var economiaDAO: EconomiaDAO
    private lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_economias, container, false)
        return v
    }

    private fun startView() {
        economiaDAO = EconomiaDAO(context!!)

        v.button_add_economia.setOnClickListener {
            createDialoagNovaEconomia()
        }

        refreshEconomias()
    }

    @SuppressLint("InflateParams")
    private fun createDialoagNovaEconomia() {
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
                val descricao = dialogView.textinput_descricao.text.toString()
                val valor = dialogView.textinput_valor.text.toString()

                addEconomia(descricao, valor)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun addEconomia(descricao: String, valor: String) {
        val economia = Economia()
        economia.descricao = descricao
        economia.valor = BigDecimal(valor)
        economia.data = Utils.currentDateFormatted()

        economiaDAO.inserir(economia)
        Toast.makeText(context, R.string.toast_nova_economia_adicionada, Toast.LENGTH_LONG).show()

        refreshEconomias()
    }

    private fun refreshEconomias() {
        val listaEconomias = economiaDAO.getTodos()
        v.listview_carteira_economias.adapter = EconomiasAdapter(this, listaEconomias)

        if (listaEconomias.isEmpty()) {
            v.tv_sem_economias.visibility = View.VISIBLE
        } else {
            v.tv_sem_economias.visibility = View.GONE
        }

    }

    fun createDialogExcluir(item: Economia): Boolean {
        AlertDialog.Builder(context!!)
            .setTitle(R.string.all_dialog_title_excluir)
            .setMessage(R.string.all_dialog_message_excluir)
            .setPositiveButton(R.string.all_string_sim) { _, _ ->
                EconomiaDAO(context!!).excluir(item)
                Toast.makeText(context, R.string.toast_carteira_pendencia_removida, Toast.LENGTH_SHORT).show()

                refreshEconomias()
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
