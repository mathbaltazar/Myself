package com.baltazarstudio.regular.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.viewpager.widget.ViewPager
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.notification.Notification
import com.baltazarstudio.regular.ui.adapter.PagerAdapter
import com.baltazarstudio.regular.ui.backup.DadosBackupActivity
import com.baltazarstudio.regular.ui.entradas.EntradasFragment
import com.baltazarstudio.regular.ui.movimentos.MovimentosFragment
import com.baltazarstudio.regular.util.Utils.Companion.gone
import com.baltazarstudio.regular.util.Utils.Companion.visible
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_content.*
import org.jetbrains.anko.intentFor

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var movimentoFragment: MovimentosFragment
    lateinit var searchMenuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        tab_layout.setupWithViewPager(vp_content_main)

        val adapter = PagerAdapter(supportFragmentManager)
        movimentoFragment = MovimentosFragment()
        adapter.addFragment(movimentoFragment, "Movimentos")
        adapter.addFragment(EntradasFragment(), "Entradas")
        vp_content_main.adapter = adapter

        vp_content_main.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    searchMenuItem.isVisible = true
                } else {
                    searchMenuItem.isVisible = false
                    searchMenuItem.collapseActionView()
                }
            }
        })

        Notification.createNotificationChannel(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        searchMenuItem = menu.findItem(R.id.action_pesquisar)

        val searchView = searchMenuItem.actionView as SearchView
        searchView.onActionViewCollapsed()
        searchView.queryHint = "Descrição..."
        searchView.setOnQueryTextListener(this)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_dados_backup -> {
                startActivity(intentFor<DadosBackupActivity>())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        movimentoFragment.filtrarDescricao(newText)
        if (newText.isNullOrBlank()) tab_layout.visible()
        else tab_layout.gone()
        return true
    }

    override fun onResume() {
        super.onResume()
        Notification.notificar(this)
    }
}
