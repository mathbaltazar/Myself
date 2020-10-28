package com.baltazarstudio.regular.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.baltazarstudio.regular.R
import com.baltazarstudio.regular.notification.Notification
import com.baltazarstudio.regular.observer.Trigger
import com.baltazarstudio.regular.observer.TriggerEvent
import com.baltazarstudio.regular.ui.backup.DadosBackupActivity
import com.baltazarstudio.regular.ui.entradas.EntradasFragment
import com.baltazarstudio.regular.ui.registros.RegistrosFragment
import com.baltazarstudio.regular.util.Utils
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {
    
    
    /**
     *
     * Próximas Atividades do APP
     *
     * - SELEÇÃO MÚLTIPLA DOS MOVIMENTOS
     * - IMPLEMENTAÇÃO DE NOTAS DE LEMBRETES
     * - INTEGRAÇÃO COM FIREBASE
     * - CRIAR/COPIAR REGISTRO A PARTIR DE OUTRO
     *
     *
      */
    




    private val movimentoFragment = RegistrosFragment()
    private val entradasFragment = EntradasFragment()
    
    var searchMenuItem: MenuItem? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar.setTitle(R.string.activity_title_meus_registros)
        setSupportActionBar(toolbar)
        
        val toggle = DrawerToggle(this, drawer_layout, toolbar)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        
        drawer_navigation_view.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_drawer_movimentos -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, movimentoFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
                    
                    toolbar.setTitle(R.string.activity_title_meus_registros)
                    searchMenuItem?.isVisible = true
                }
                R.id.menu_drawer_entradas -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, entradasFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
                    
                    toolbar.setTitle(R.string.activity_title_entradas)
                    searchMenuItem?.isVisible = false
                    searchMenuItem?.collapseActionView()
                }
            }
            
            menuItem.isChecked = true
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }
        
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, movimentoFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
        drawer_navigation_view.setCheckedItem(R.id.menu_drawer_movimentos)
        
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        searchMenuItem = menu.findItem(R.id.action_pesquisar)
        
        val searchView = searchMenuItem!!.actionView as SearchView
        searchView.onActionViewCollapsed()
        searchView.queryHint = "Descrição..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                movimentoFragment.filtrarDescricao(newText)
                return true
            }
        })
        
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
    
    private fun setupFirebaseMessaging() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@addOnCompleteListener
                }
            
                Log.w("FCM Token Registration", task.result)
            }
    }
    
    private fun registerToastNotification() {
        Trigger.watcher().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { t ->
                if (t is TriggerEvent.Toast) {
                    toast(t.message)
                }
            }.apply {  }
    }
    
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    
    override fun onStart() {
        super.onStart()
    
        setupFirebaseMessaging()
        registerToastNotification()
    }
    
    override fun onResume() {
        super.onResume()
        Notification.notificar(this)
    }
    
    private class DrawerToggle(
        activity: Activity?,
        private val drawerLayout: DrawerLayout?,
        toolbar: Toolbar?
    ) : ActionBarDrawerToggle(
        activity, drawerLayout, toolbar, android.R.string.yes, android.R.string.cancel
    ) {
        
        
        override fun onDrawerOpened(drawerView: View) {
            super.onDrawerOpened(drawerView)
            drawerView.bringToFront()
            drawerLayout?.requestLayout()
        }
        
    }
}
