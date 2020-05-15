package com.baltazarstudio.regular.ui.pendencia

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.adapter.MovimentoAdapter
import com.baltazarstudio.regular.database.dao.MovimentoDAO
import com.baltazarstudio.regular.model.Movimento
import kotlinx.android.synthetic.main.fragment_movimentos.view.*
import org.jetbrains.anko.*
import java.util.*


class MovimentosFragment : Fragment() {

    private lateinit var movimentoDAO: MovimentoDAO
    private lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v = inflater.inflate(R.layout.fragment_movimentos, container, false)

        movimentoDAO = MovimentoDAO(v.context)

        setUpView()
        setUpMovimentos()

        return v
    }

    private fun setUpView() {

        v.button_add_movimento.setOnClickListener {
            val dialog = CriarMovimentoDialog(v.context, childFragmentManager)
            dialog.setOnDismissListener {
                setUpMovimentos()
            }
            dialog.show()
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

        v.apply {
            ll_movimentos.removeAllViews()

            val lp = ViewGroup.LayoutParams(matchParent, wrapContent)

            for (ano in movimentoDAO.getAnosDisponiveis()) {
                for (mes in movimentoDAO.getMesDisponivelPorAno(ano)) {

                    val header = TextView(context)
                    header.layoutParams = lp
                    header.padding = dip(8)
                    header.text = String.format("%s/%d", getMesString(mes), ano)
                    header.textColor = Color.BLACK
                    header.typeface = Typeface.DEFAULT_BOLD
                    header.backgroundColorResource = R.color.off_white

                    val adapter = MovimentoAdapter(
                        context,
                        itensMovimentos.filter { it.mes == mes && it.ano == ano },
                        excluir
                    )
                    val recyclerView = RecyclerView(context)
                    recyclerView.layoutParams = lp
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.isNestedScrollingEnabled = false
                    recyclerView.addItemDecoration(
                        DividerItemDecoration(
                            context,
                            DividerItemDecoration.VERTICAL
                        )
                    )

                    ll_movimentos.addView(header)
                    ll_movimentos.addView(recyclerView)
                }
            }

        }
    }

    private fun getMesString(mes: Int): String {
        return when (mes) {
            Calendar.JANUARY -> "JANEIRO"
            Calendar.FEBRUARY -> "FEVEREIRO"
            Calendar.MARCH -> "MARÇO"
            Calendar.APRIL -> "ABRIL"
            Calendar.MAY -> "MAIO"
            Calendar.JUNE -> "JUNHO"
            Calendar.JULY -> "JULHO"
            Calendar.AUGUST -> "AGOSTO"
            Calendar.SEPTEMBER -> "SETEMBRO"
            Calendar.OCTOBER -> "OUTUBRO"
            Calendar.NOVEMBER -> "NOVEMBRO"
            Calendar.DECEMBER -> "DEZEMBRO"
            else -> ""
        }
    }

    override fun onResume() {
        super.onResume()

        if (activity?.intent?.action == "abrir_adicionar_movimento")
            CriarMovimentoDialog(v.context, childFragmentManager).show()
    }
}
