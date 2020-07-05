package com.baltazarstudio.regular.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.model.Movimento
import com.baltazarstudio.regular.util.Utils
import com.baltazarstudio.regular.util.Utils.Companion.formattedDate
import kotlinx.android.synthetic.main.layout_header_movimento.view.*
import kotlinx.android.synthetic.main.layout_item_movimento.view.*
import java.util.*

class MovimentoAdapter(
    context: Context,
    private var pairMesAno: Pair<Int, Int>,
    private var itens: List<Movimento>,
    private var excluirMovimento: (Movimento) -> AlertDialog
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)
    private val HEADER_VIEW_TYPE = 100

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == HEADER_VIEW_TYPE)
            return HeaderViewHolder(
                layoutInflater.inflate(
                    R.layout.layout_header_movimento,
                    parent,
                    false
                )
            )
        return MovimentoViewHolder(
            layoutInflater.inflate(
                R.layout.layout_item_movimento,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return itens.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder)
            holder.bindHeader()
        else
            (holder as MovimentoViewHolder).bindView(position - 1)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0)
            return HEADER_VIEW_TYPE
        return super.getItemViewType(position)
    }

    private inner class MovimentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val calendar = Calendar.getInstance()

        fun bindView(position: Int) {
            val movimento = itens[position]
            itemView.tv_movimento_descricao.text = movimento.descricao
            itemView.tv_movimento_valor.text = Utils.formatCurrency(movimento.valor)

            calendar.set(Calendar.DAY_OF_MONTH, movimento.dia)
            calendar.set(Calendar.MONTH, movimento.mes)
            calendar.set(Calendar.YEAR, movimento.ano)

            itemView.tv_movimento_data.text = calendar.formattedDate()

            itemView.setOnLongClickListener {
                excluirMovimento(movimento)
                true
            }
        }
    }

    private inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindHeader() {
            itemView.tv_header_movimento_title.text =
                String.format("%s/%d", getMesString(pairMesAno.first), pairMesAno.second)

            var total = 0.0
            for (movimento in itens) {
                total += movimento.valor
            }
            itemView.tv_header_movimento_total.text = Utils.formatCurrency(total)
        }
    }

    companion object {
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
    }
}
