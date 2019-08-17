package com.baltazarstudio.regular.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.model.Economia
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.list_item_economias.view.*

class EconomiasAdapter(var context: Context, var lista: List<Economia>) : BaseAdapter() {
    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, p2: ViewGroup?): View {
        val holder: ViewHolder
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_economias, null)
            holder = ViewHolder()
            holder.descricao = view.tv_item_economia_descricao
            holder.valor = view.tv_item_economia_valor
//            holder.data = view.tv_item_economia_data
        } else {
            holder = view.tag as ViewHolder
        }

        val economia = lista[position]
        holder.descricao!!.text = economia.descricao
        holder.valor!!.text = Utils.formatCurrency(economia.valor)
//        holder.data!!.text = economia.data


        view!!.tag = holder
        return view
    }

    override fun getItem(position: Int): Any {
        return lista[position]
    }

    override fun getItemId(p0: Int): Long {
        return 0L
    }

    override fun getCount(): Int {
        return lista.size
    }

}

open class ViewHolder {
    var descricao: TextView? = null
    var valor: TextView? = null
    //var data: TextView? = null
}
