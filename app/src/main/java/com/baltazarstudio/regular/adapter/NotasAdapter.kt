package com.baltazarstudio.regular.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.ui.DetalhesPendenciaActivity
import kotlinx.android.synthetic.main.detalhes_pendencia_item_nota.view.*

class NotasAdapter(private var context: Context, private var notas: ArrayList<String>) : BaseAdapter() {
    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val holder: ViewHolder
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.detalhes_pendencia_item_nota, null)
            holder = ViewHolder()
            holder.lblNota = view.label_item_nota_descricao
            holder.btnExcluir = view.imagebutton_item_nota_excluir
        } else {
            holder = view.tag as ViewHolder
        }

        val nota = notas[position]
        holder.lblNota!!.text = nota
        holder.btnExcluir!!.setOnClickListener {
            (context as DetalhesPendenciaActivity).excluirNota(nota)
        }

        view!!.tag = holder
        return view
    }

    override fun getItem(position: Int): Any {
        return notas[position]
    }

    override fun getItemId(position: Int): Long {
        return 0L
    }

    override fun getCount(): Int {
        return notas.size
    }


    private open class ViewHolder {
        var lblNota: AppCompatTextView? = null
        var btnExcluir: AppCompatImageView? = null
    }
}
