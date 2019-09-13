package com.baltazarstudio.regular.ui.pendencia

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
        PendenciaCreateDialog(context!!).show()
    }

    fun confirmDialogExcluir(item: Pendencia): Boolean {
        AlertDialog.Builder(context!!)
                .setTitle(R.string.all_dialog_title_excluir)
                .setMessage(R.string.all_dialog_message_excluir)
                .setPositiveButton(R.string.all_string_sim) { _, _ ->
                    pendenciaDAO.excluir(item)
                    Toast.makeText(context, R.string.toast_pendencia_removida, Toast.LENGTH_SHORT).show()

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
