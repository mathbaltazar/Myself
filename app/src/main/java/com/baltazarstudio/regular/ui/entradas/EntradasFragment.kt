package com.baltazarstudio.regular.ui.entradas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.database.dao.EntradaDAO
import com.baltazarstudio.regular.ui.adapter.EntradasAdapter
import kotlinx.android.synthetic.main.fragment_entradas.view.*

class EntradasFragment : Fragment() {

    private lateinit var entradaDAO: EntradaDAO
    private lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_entradas, container, false)

        entradaDAO = EntradaDAO(v.context)
        setupView()

        return v
    }

    private fun setupView() {
        val adapter = ArrayAdapter(v.context, android.R.layout.simple_spinner_item, listaDonos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        v.spinner_entradas_dono.adapter = adapter
        v.spinner_entradas_dono.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    v.rv_entradas.adapter =
                        EntradasAdapter(v.context, entradaDAO, listaDonos[position])
                }
            }

        v.rv_entradas.layoutManager = LinearLayoutManager(v.context)

        v.button_entradas_add.setOnClickListener {
            val dialog =
                NovaEntradaDialog(v.context, entradaDAO, listaDonos.minusElement(TODOS))
            dialog.show()
        }
    }

    companion object {
        const val TODOS = "Todos"
        private val listaDonos = arrayListOf(TODOS, "Matheus", "Márcia")
    }

}