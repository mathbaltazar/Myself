package com.baltazarstudio.regular.ui.pendencia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.adapter.MovimentoAdapter
import com.baltazarstudio.regular.database.dao.MovimentoDAO
import com.baltazarstudio.regular.model.Movimento
import kotlinx.android.synthetic.main.fragment_movimentos.view.*


class MovimentosFragment : Fragment() {

    private lateinit var movimentoDAO: MovimentoDAO
    private lateinit var v: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        v = inflater.inflate(R.layout.fragment_movimentos, container, false)

        movimentoDAO = MovimentoDAO(v.context)

        setUpView()
        setUpMovimentos()

        return v
    }

    private fun setUpView() {

        v.button_add_movimento.setOnClickListener {
            CriarMovimentoDialog(v.context).show()
        }

    }

    private fun setUpMovimentos() {
        val itensMovimentos = movimentoDAO.getTodosMovimentos()

        val excluir = { item: Movimento ->
            AlertDialog.Builder(context!!)
                .setTitle("Excluir")
                .setMessage("Confirmar exclusão")
                .setPositiveButton("Sim") { _, _ ->
                    movimentoDAO.excluir(item)
                    Toast.makeText(context, "Removido!", Toast.LENGTH_SHORT).show()
                    setUpMovimentos()
                }
                .setNegativeButton("Não", null)
                .show()
        }

        v.rv_movimentos.adapter = MovimentoAdapter(v.context, itensMovimentos, excluir)

    }
}
