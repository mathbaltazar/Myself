package com.baltazarstudio.regular.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.adapter.PagerAdapter
import com.baltazarstudio.regular.notification.Notification
import com.baltazarstudio.regular.ui.pendencia.MovimentosFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        tab_layout.setupWithViewPager(vp_content_main)

        val adapter = PagerAdapter(supportFragmentManager)
        val fragment = MovimentosFragment()
        adapter.addFragment(fragment, "Movimentos")
        vp_content_main.adapter = adapter

        Notification.createNotificationChannel(this)
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

    override fun onResume() {
        super.onResume()
        Notification.notificar(this)
    }
}
