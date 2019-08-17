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
import com.baltazarstudio.regular.model.CarteiraPendencia
import com.baltazarstudio.regular.ui.CarteiraAbertaFragment
import com.baltazarstudio.regular.ui.DetalhesItemCarteiraActivity
import com.baltazarstudio.regular.util.Utils
import kotlinx.android.synthetic.main.list_item_carteira_aberta_pendencia.view.*

class CarteiraPendenciaAdapter(private var fragment: Fragment,
                               private var itens: List<CarteiraPendencia>) : BaseAdapter() {


    private val context = fragment.context

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
        val holder: ViewHolder
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_carteira_aberta_pendencia, null)
            holder = ViewHolder()
            holder.descricao = view.tv_item_carteira_descricao
            holder.valor = view.tv_item_carteira_valor
            holder.frame = view.layout_item_pendencia
        } else {
            holder = view.tag as ViewHolder
        }


        val pendencia = itens[position]
        holder.descricao!!.text = pendencia.descricao
        holder.valor!!.text = Utils.formatCurrency(pendencia.valor)
        holder.frame!!.setOnClickListener {
            val i = Intent(context, DetalhesItemCarteiraActivity::class.java)
            i.putExtra("id", pendencia.id)
            context!!.startActivity(i)
        }
        holder.frame!!.setOnLongClickListener {
            (fragment as CarteiraAbertaFragment).createDialogExcluir(pendencia)
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
            var descricao: TextView? = null
            var valor: TextView? = null
            var frame: ViewGroup? = null
        }
    }

}
