package com.baltazarstudio.regular.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.model.ItemCarteiraAberta
import kotlinx.android.synthetic.main.item_recycler_carteira_aberta.view.*
import java.text.NumberFormat
import java.util.*

class ItemCarteiraRecyclerAdapter(private var context: Context,
                                  private var itens: List<ItemCarteiraAberta>) : BaseAdapter() {


    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
        val holder: ViewHolder
        if (convertView == null) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_recycler_carteira_aberta, null)
            holder = ViewHolder()
            holder.descricao = view.tv_item_carteira_descricao
            holder.valor = view.tv_item_carteira_valor

            bindView(holder, position)
            view.tag = holder
            return view
        } else {
            holder = convertView.tag as ViewHolder
        }

        bindView(holder, position)
        convertView.tag = holder
        return convertView
    }

    override fun getItem(position: Int): Any {
        return itens[position]
    }

    override fun getItemId(p0: Int): Long {
        return 0L
    }

    override fun getCount(): Int {
        return itens.size
    }

    private fun bindView(holder: ViewHolder, position: Int) {
        val itemCarteira = itens[position]
        holder.descricao!!.text = itemCarteira.descricao
        holder.valor!!.text = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                .format(itemCarteira.valor)
    }

    companion object {
        private class ViewHolder {
            var descricao: TextView? = null
            var valor: TextView? = null
        }
    }

}
