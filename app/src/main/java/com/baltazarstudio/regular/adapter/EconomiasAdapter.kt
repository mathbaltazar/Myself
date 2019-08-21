package com.baltazarstudio.regular.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.model.Economia
import com.baltazarstudio.regular.ui.DetalhesEconomiaActivity
import com.baltazarstudio.regular.ui.EconomiasFragment
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.list_item_economias.view.*

class EconomiasAdapter(private var fragment: Fragment, private var lista: List<Economia>) : BaseAdapter() {

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, p2: ViewGroup?): View {
        var view = convertView
        val holder: ViewHolder
        if (view == null) {
            view = LayoutInflater.from(fragment.context).inflate(R.layout.list_item_economias, null)
            holder = ViewHolder()
            holder.lblDescricao = view.tv_item_economia_descricao
            holder.lblValor = view.tv_item_economia_valor
            holder.lblValorPoupanca = view.tv_item_economia_valor_poupanca
//            holder.data = view.tv_item_economia_data
            holder.frame = view.layout_item_economia
        } else {
            holder = view.tag as ViewHolder
        }

        val economia = lista[position]
        holder.lblDescricao!!.text = economia.descricao
        holder.lblValor!!.text = Utils.formatCurrency(economia.valor)
        holder.lblValorPoupanca!!.text = Utils.formatCurrency(economia.valorPoupanca)
//        holder.data!!.text = economia.data

        holder.frame!!.setOnClickListener { _ ->
            val i = Intent(fragment.context, DetalhesEconomiaActivity::class.java)
            i.putExtra("id", economia.id)
            fragment.startActivity(i)
        }

        holder.frame!!.setOnLongClickListener { _ ->
            (fragment as EconomiasFragment).createDialogExcluir(economia)
        }


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

private class ViewHolder {
    var lblDescricao: TextView? = null
    var lblValor: TextView? = null
    var lblValorPoupanca: TextView? = null
    var frame: ViewGroup? = null
}
