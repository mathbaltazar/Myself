package com.baltazarstudio.regular.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.adapter.ItemCarteiraRecyclerAdapter
import com.baltazarstudio.regular.adapter.MainPagerAdapter
import com.baltazarstudio.regular.database.ItemCarteiraAbertaDAO
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.page_carteira_aberta.view.*

class MainActivity : AppCompatActivity() {

    val carteiraAbertaDAO = ItemCarteiraAbertaDAO(this)


    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        //toolbar.title = "Controles"


        val viewCarteiraAberta = layoutInflater.inflate(R.layout.page_carteira_aberta, null)
        setupViewCarteiraAberta(viewCarteiraAberta)

        val viewEconomias = layoutInflater.inflate(R.layout.page_economias, null)
        setupViewEconomias(viewEconomias)

        val viewPages = arrayListOf(viewCarteiraAberta, viewEconomias)
        vp_content_main.adapter = MainPagerAdapter(viewPages)
        tab_layout.setupWithViewPager(vp_content_main)

    }


    private fun setupViewCarteiraAberta(view: View) {
        view.tag = getString(R.string.view_pager_title_carteira)

        view.btn_toggle_add_item_carteira.setOnClickListener {
            if (view.layout_add_item_carteira.visibility != View.VISIBLE) {
                view.layout_add_item_carteira.visibility = View.VISIBLE
                view.divider_add_item_carteira.visibility = View.VISIBLE
                view.btn_toggle_add_item_carteira.setImageResource(android.R.drawable.ic_delete)
            } else {
                view.layout_add_item_carteira.visibility = View.GONE
                view.divider_add_item_carteira.visibility = View.GONE
                view.btn_toggle_add_item_carteira.setImageResource(android.R.drawable.ic_input_add)
                hideKeyBoard()
            }
        }

        view.btn_register_item_carteira.setOnClickListener {
            Toast.makeText(this, "Adicionado!", Toast.LENGTH_LONG).show()
            view.btn_toggle_add_item_carteira.performClick()
        }


        val itensCarteiraAberta = carteiraAbertaDAO.getTodos()

        if (itensCarteiraAberta.size == 0) {
            view.tv_sem_pendencias.visibility = View.VISIBLE
        } else {
            view.recycler_carteira_aberta.adapter = ItemCarteiraRecyclerAdapter(this, itensCarteiraAberta)
            view.recycler_carteira_aberta.layoutManager = LinearLayoutManager(this)
        }

    }

    private fun setupViewEconomias(view: View) {
        view.tag = getString(R.string.view_pager_title_economias)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //R.id.action_settings ->
        }
        return super.onOptionsItemSelected(item)
    }

    private fun hideKeyBoard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE)
    }
}
