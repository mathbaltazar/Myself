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
import kotlinx.android.synthetic.main.item_carteira_aberta.view.*

class CarteiraPendenciaAdapter(private var fragment: Fragment,
                               private var itens: List<CarteiraPendencia>) : BaseAdapter() {


    private val context = fragment.context

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
        val holder: ViewHolder
        if (convertView == null) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_carteira_aberta, null)
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

    private fun bindView(holder: ViewHolder, carteira: CarteiraPendencia) {
        holder.descricao!!.text = carteira.descricao
        holder.valor!!.text = Utils.formatCurrency(carteira.valor)
        holder.frame!!.setOnClickListener {
            val i = Intent(context, DetalhesItemCarteiraActivity::class.java)
            i.putExtra("id", carteira.id)
            context!!.startActivity(i)
        }
        holder.frame!!.setOnLongClickListener {
            (fragment as CarteiraAbertaFragment).createDialogExcluir(carteira)
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
