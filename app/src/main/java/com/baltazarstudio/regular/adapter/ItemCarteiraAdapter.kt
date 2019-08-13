package com.baltazarstudio.regular.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.model.ItemCarteiraAberta
import com.baltazarstudio.regular.ui.DetalhesItemCarteiraActivity
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.item_carteira_aberta.view.*

class ItemCarteiraAdapter(private var activity: Activity,
                          private var itens: List<ItemCarteiraAberta>) : BaseAdapter() {


    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
        val holder: ViewHolder
        if (convertView == null) {
            val view = LayoutInflater.from(activity.baseContext).inflate(R.layout.item_carteira_aberta, null)
            holder = ViewHolder()
            holder.descricao = view.tv_item_carteira_descricao
            holder.valor = view.tv_item_carteira_valor
            holder.frame = view.layout_item_carteira

            bindView(holder, itens[position])
            view.tag = holder
            return view
        } else {
            holder = convertView.tag as ViewHolder
        }

        bindView(holder, itens[position])
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

    private fun bindView(holder: ViewHolder, itemCarteira: ItemCarteiraAberta) {
        holder.descricao!!.text = itemCarteira.descricao
        holder.valor!!.text = Utils.formatCurrency(itemCarteira.valor)
        holder.frame!!.setOnClickListener {
            val i = Intent(activity, DetalhesItemCarteiraActivity::class.java)
            i.putExtra("id", itemCarteira.id)
            activity.startActivity(i)
        }
    }

    companion object {
        private class ViewHolder {
            var descricao: TextView? = null
            var valor: TextView? = null
            var frame: ViewGroup? = null
        }
    }

}
