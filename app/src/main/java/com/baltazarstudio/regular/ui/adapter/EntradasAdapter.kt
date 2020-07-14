package com.baltazarstudio.regular.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.database.dao.EntradaDAO
import com.baltazarstudio.regular.model.Entrada
import com.baltazarstudio.regular.ui.entradas.EntradasFragment
import com.baltazarstudio.regular.util.Utils
import com.baltazarstudio.regular.util.Utils.Companion.formattedDate
import kotlinx.android.synthetic.main.list_item_entradas.view.*
import org.jetbrains.anko.toast

class EntradasAdapter(context: Context, private val dao: EntradaDAO, dono: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val layoutInflater: LayoutInflater
    private val entradas: ArrayList<Entrada>
    private var expanded = hashSetOf<Int>()

    init {
        this.layoutInflater = LayoutInflater.from(context)
        if (dono.equals(EntradasFragment.TODOS))
            this.entradas = dao.getTodasEntradas()
        else
            this.entradas = dao.getTodasEntradas(dono)
    }

    override fun getItemCount(): Int {
        return entradas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(layoutInflater.inflate(R.layout.list_item_entradas, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bindView(position)
    }

    private inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindView(position: Int) {
            val entrada = entradas[position]

            itemView.setOnClickListener {
                if (expanded.contains(position))
                    expanded.remove(position)
                else
                    expanded.add(position)
                notifyItemChanged(position)
            }

            itemView.setOnLongClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle("Atenção")
                    .setMessage("Confirma a exclusão da entrada?")
                    .setPositiveButton("Sim") { _, _ ->
                        dao.remover(entrada)
                        itemView.context.toast("Excluído!")
                        entradas.remove(entrada)
                        notifyItemRemoved(adapterPosition)
                    }
                    .setNegativeButton("Não", null)
                    .show()
                true
            }

            if (expanded.contains(position)) {
                itemView.ll_item_entradas_info.visibility = View.VISIBLE
                itemView.iv_item_entradas_expand_arrow.setImageResource(R.drawable.ic_arrow_up)
            } else {
                itemView.ll_item_entradas_info.visibility = View.GONE
                itemView.iv_item_entradas_expand_arrow.setImageResource(R.drawable.ic_arrow_down)
            }

            itemView.tv_item_entradas_valor.text = Utils.formatCurrency(entrada.valor)
            itemView.tv_item_entradas_dono.text = entrada.dono
            itemView.tv_item_entradas_descricao.text = entrada.descricao
            itemView.tv_item_entradas_data.text = entrada.data?.formattedDate()

        }
    }
}
