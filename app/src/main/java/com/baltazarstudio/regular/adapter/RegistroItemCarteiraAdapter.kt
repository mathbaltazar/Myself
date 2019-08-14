package com.baltazarstudio.regular.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.model.RegistroItem
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.list_item_registro_carteira.view.*

class RegistroItemCarteiraAdapter(var context: Context,
                                  var registros: List<RegistroItem>
) : BaseAdapter() {

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
        val holder: ViewHolder
        if (convertView == null) {
            val view = LayoutInflater.from(context).inflate(R.layout.list_item_registro_carteira, null)
            holder = ViewHolder()
            holder.descricao = view.tv_item_carteira_registro_descricao
            holder.valor = view.tv_item_carteira_registro_valor

            bindView(holder, registros[position])
            view.tag = holder
            return view
        } else {
            holder = convertView.tag as ViewHolder
        }

        bindView(holder, registros[position])
        convertView.tag = holder
        return convertView
    }

    override fun getItem(position: Int): Any {
        return registros[position]
    }

    override fun getItemId(p0: Int): Long {
        return 0L
    }

    override fun getCount(): Int {
        return registros.size
    }

    private fun bindView(holder: ViewHolder, registro: RegistroItem) {
        holder.descricao!!.text = registro.descricao
        holder.valor!!.text = Utils.formatCurrency(registro.valor)
    }

    companion object {
        private class ViewHolder {
            var descricao: TextView? = null
            var valor: TextView? = null
        }
    }
}
