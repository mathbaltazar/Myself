package com.baltazarstudio.regular.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.adapter.MainPagerAdapter
import com.baltazarstudio.regular.database.ItemCarteiraAbertaDAO
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*

class MainActivity : AppCompatActivity() {

    val carteiraAbertaDAO = ItemCarteiraAbertaDAO(this)


    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        //toolbar.title = "Controles"

        tab_layout.setupWithViewPager(vp_content_main)

        val adapter = MainPagerAdapter(supportFragmentManager)
        adapter.addFragment(CateiraAbertaFragment(), getString(R.string.view_pager_fragment_title_carteira))
        adapter.addFragment(EconomiasFragment(), getString(R.string.view_pager_fragment_title_economias))
        vp_content_main.adapter = adapter


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
}
