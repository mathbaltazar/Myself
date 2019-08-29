package com.baltazarstudio.regular.ui

import android.annotation.SuppressLint
import android.content.Context
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
import com.baltazarstudio.regular.util.CurrencyMask
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.dialog_add_economia.view.*
import kotlinx.android.synthetic.main.fragment_economias.view.*

class EconomiasFragment(context: Context) : Fragment() {

    private val economiaDAO = EconomiaDAO(context)
    private lateinit var v: View

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_economias, container, false)
        setup()
        return v
    }

    private fun setup() {
        v.button_add_economia.setOnClickListener {
            createDialoagNovaEconomia()
        }
    }

    @SuppressLint("InflateParams")
    private fun createDialoagNovaEconomia() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_economia, null)
        val dialog = AlertDialog.Builder(context!!)
                .setView(dialogView)
                .create()

        dialogView.textinput_dialog_add_economia_valor.addTextChangedListener(
                CurrencyMask(dialogView.textinput_dialog_add_economia_valor)
        )

        dialogView.button_dialog_economia_adicionar.setOnClickListener {
            if (dialogView.textinput_dialog_add_economia_descricao.text.toString() == ""
                    || dialogView.textinput_dialog_add_economia_valor.text.toString() == "") {

                dialogView.label_dialog_add_economia_error.visibility = View.VISIBLE
            } else {
                val descricao = dialogView.textinput_dialog_add_economia_descricao.text.toString()
                val valor = dialogView.textinput_dialog_add_economia_valor.text.toString()

                val economia = Economia()
                economia.descricao = descricao
                economia.valor = Utils.unformatCurrency(valor).toBigDecimal()
                economia.data = Utils.currentDateFormatted()

                economiaDAO.inserir(economia)
                Toast.makeText(context, R.string.toast_nova_economia_adicionada, Toast.LENGTH_LONG).show()

                refreshEconomiasAtivas()

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun refreshEconomiasAtivas() {
        val listaEconomiasAtivas = economiaDAO.getTodos().filter { !it.conquistado }
        v.listview_carteira_economias.adapter = EconomiasAdapter(this, listaEconomiasAtivas)

        if (listaEconomiasAtivas.isEmpty()) {
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

                    refreshEconomiasAtivas()
                }
                .setNegativeButton(R.string.all_string_nao, null)
                .show()
        return true
    }

    override fun onResume() {
        super.onResume()
        refreshEconomiasAtivas()
    }
}
