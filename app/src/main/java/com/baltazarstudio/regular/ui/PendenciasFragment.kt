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
import com.baltazarstudio.regular.adapter.PendenciasAdapter
import com.baltazarstudio.regular.database.dao.PendenciaDAO
import com.baltazarstudio.regular.model.Pendencia
import com.baltazarstudio.regular.util.CurrencyMask
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.dialog_add_pendencia.view.*
import kotlinx.android.synthetic.main.fragment_pendecias.view.*


class PendenciasFragment(context: Context) : Fragment() {


    private val pendenciaDAO = PendenciaDAO(context)
    private lateinit var v: View

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_pendecias, container, false)
        setup()
        return v
    }

    private fun setup() {
        v.button_add_pendencia.setOnClickListener {
            createDialogNovaPendencia()
        }

        refreshPendencias()
    }

    private fun refreshPendencias() {
        val listItensPendencias = pendenciaDAO.getTodasPendencias().filter { !it.pago }
        v.listview_carteira_pendencias.adapter = PendenciasAdapter(this, listItensPendencias)

        if (listItensPendencias.isEmpty()) {
            v.tv_sem_pendencias.visibility = View.VISIBLE
        } else {
            v.tv_sem_pendencias.visibility = View.GONE
        }
    }

    @SuppressLint("InflateParams")
    private fun createDialogNovaPendencia() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_pendencia, null)
        val dialog = AlertDialog.Builder(context!!)
                .setView(dialogView)
                .create()

        val textinputValor = dialogView.textinput_dialog_add_pendencia_valor
        textinputValor.addTextChangedListener(CurrencyMask(textinputValor))

        dialogView.button_dialog_add_pendencia_adicionar.setOnClickListener {
            if (dialogView.textinput_dialog_add_pendencia_descricao.text.toString() == ""
                    || dialogView.textinput_dialog_add_pendencia_valor.text.toString() == "") {
                dialogView.label_dialog_add_pendencia_error.visibility = View.VISIBLE
            } else {
                val descricao = dialogView.textinput_dialog_add_pendencia_descricao.text.toString()
                val valor = dialogView.textinput_dialog_add_pendencia_valor.text.toString()

                val item = Pendencia()
                item.descricao = descricao
                item.valor = Utils.unformatCurrency(valor).toBigDecimal()
                item.data = Utils.currentDateFormatted()
                pendenciaDAO.inserir(item)

                Toast.makeText(context, R.string.toast_carteira_pendencia_adicionada, Toast.LENGTH_LONG).show()
                dialog.dismiss()

                refreshPendencias()
            }
        }

        dialog.show()
    }

    fun createDialogExcluir(item: Pendencia): Boolean {
        AlertDialog.Builder(context!!)
                .setTitle(R.string.all_dialog_title_excluir)
                .setMessage(R.string.all_dialog_message_excluir)
                .setPositiveButton(R.string.all_string_sim) { _, _ ->
                    pendenciaDAO.excluir(item)
                    Toast.makeText(context, R.string.toast_carteira_pendencia_removida, Toast.LENGTH_SHORT).show()

                    refreshPendencias()
                }
                .setNegativeButton(R.string.all_string_nao, null)
                .show()
        return true
    }

    override fun onResume() {
        super.onResume()
        refreshPendencias()
    }
}
