package com.baltazarstudio.regular.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.model.Pendencia
import com.baltazarstudio.regular.ui.pendencia.DetalhesPendenciaDialog
import com.baltazarstudio.regular.ui.pendencia.PendenciasFragment
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.list_item_pendencia.view.*

class PendenciasAdapter(private var fragment: Fragment,
                        private var itens: List<Pendencia>) : BaseAdapter() {


    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
        val holder: ViewHolder
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(fragment.context).inflate(R.layout.list_item_pendencia, null)
            holder = ViewHolder(view)
        } else {
            holder = view.tag as ViewHolder
        }

        val pendencia = itens[position]
        holder.lblDescricao.text = pendencia.descricao
        holder.lblValor.text = Utils.formatCurrency(pendencia.valor)
        holder.lblData.text = pendencia.data

        holder.layout.setOnClickListener {
            val dialog = DetalhesPendenciaDialog(fragment.context!!, pendencia)
            dialog.setOnDismissListener {

            }
            dialog.show()
        }

        holder.layout.setOnLongClickListener {
            (fragment as PendenciasFragment).confirmDialogExcluir(pendencia)
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
        private class ViewHolder(itemView: View) {
            val lblDescricao: TextView = itemView.tv_item_pendencia_descricao
            val lblValor: TextView = itemView.tv_item_pendencia_valor
            val lblData: TextView = itemView.tv_item_pendencia_data
            val layout: LinearLayout = itemView.layout_item_pendencia
        }
    }
}
