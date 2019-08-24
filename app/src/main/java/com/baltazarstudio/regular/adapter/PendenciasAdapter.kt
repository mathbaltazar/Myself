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
import com.baltazarstudio.regular.model.Pendencia
import com.baltazarstudio.regular.ui.DetalhesPendenciaActivity
import com.baltazarstudio.regular.ui.PendenciasFragment
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.list_item_pendencia.view.*

class PendenciasAdapter(private var fragment: Fragment,
                        private var itens: List<Pendencia>) : BaseAdapter() {


    private val context = fragment.context

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
        val holder: ViewHolder
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_pendencia, null)
            holder = ViewHolder()
            holder.lblDescricao = view.tv_item_pendencia_descricao
            holder.lblValor = view.tv_item_pendencia_valor
            holder.lblData = view.tv_item_pendencia_data

            holder.frame = view.layout_item_pendencia
        } else {
            holder = view.tag as ViewHolder
        }


        val pendencia = itens[position]
        holder.lblDescricao!!.text = pendencia.descricao
        holder.lblValor!!.text = Utils.formatCurrency(pendencia.valor)
        holder.lblData!!.text = pendencia.data
        holder.frame!!.setOnClickListener {
            val i = Intent(context, DetalhesPendenciaActivity::class.java)
            i.putExtra("id", pendencia.id)
            context!!.startActivity(i)
        }
        holder.frame!!.setOnLongClickListener {
            (fragment as PendenciasFragment).createDialogExcluir(pendencia)
        }

        view!!.tag = holder
        return view
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

    companion object {
        private class ViewHolder {
            var lblDescricao: TextView? = null
            var lblValor: TextView? = null
            var lblData: TextView? = null
            var frame: ViewGroup? = null
        }
    }

}
