package com.baltazarstudio.regular.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.model.ItemCarteiraAberta

class ItemCarteiraRecyclerAdapter(var context: Context, var itens: List<ItemCarteiraAberta>)
    : RecyclerView.Adapter<ItemCarteiraRecyclerAdapter.ItemViewHolder>() {


    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recycler_carteira_aberta, null)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itens.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(itens[position])
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(itemCarteira: ItemCarteiraAberta) {

        }
    }

}
