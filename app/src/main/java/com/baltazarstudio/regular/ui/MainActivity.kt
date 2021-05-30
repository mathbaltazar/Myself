package com.baltazarstudio.regular.ui

import android.app.Activity
import android.content.Context
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
import com.baltazarstudio.regular.context.RegistroContext
import com.baltazarstudio.regular.notification.Notification
import com.baltazarstudio.regular.observer.Events
import com.baltazarstudio.regular.observer.Trigger
import com.baltazarstudio.regular.ui.backup.DadosBackupFragment
import com.baltazarstudio.regular.ui.entradas.EntradasFragment
import com.baltazarstudio.regular.ui.movimentacao.MovimentacaoFragment
import com.baltazarstudio.regular.ui.resumo.ResumoFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    
    
    var searchMenuItem: MenuItem? = null
    var addMenuItem: MenuItem? = null
    
    /**
     *
     * Próximas Atividades do APP
     *
     * - (DONE) SELEÇÃO MÚLTIPLA DOS MOVIMENTOS
     * - LIVE SCROLL PARA REGISTROS/DESPESAS
     * - (DONE) RESUMO DE TODOS OS DADOS + FILTROS VARIADOS
     * - (DONE) FORMA MAIS VIÁVEL/PRÁTICA DE CAPTURAR DATA
     * - (DONE) APONTAR NO POPUP DOS REGISTROS SE PERTENCE A ALGUMA DESPESA
     * - PIN de acesso regular
     * - (DONE) Editar despesa (Perguntar se registros serão alterados também)
     * - (DONE) Nova Fonte
     * - (DONE) Manutenção: Alterar registro pelo "Ver Todos" da aba despesa não altera o popup
     * - (DONE) Despesa: Agregar/desagregar registro existente
     * - (DONE) Despesa: Dia do vencimento
     * - (DONE) Despesa: Quantidade Registros
     * - (DONE) Manutenção: Inverter ordem resultado das pesquisas registros
     * - (DONE) Manutenção: Alterar ou excluir registro enquanto há pesquisa, volta todos registros ao invés da última pesquisa
     * - (DONE) Manutenção: Inverter ordem resultado dos registros "Ver Todos" das despesas
     * - (DONE) Correção: Dropdown dia vencimento não se ajustando a diferente resoluções
     *
     * BUGS ---> (DONE) ADAPTAR À TODAS AS UI NECESSÁRIAS QUANDO UM REGISTRO/DESPESA FOR ALTERADO
     * BUGS ---> (DONE) NOVO DESIGN DESPESA: NOVA TELA PARA DETALHES DA DESPESA (TENTAR "ICONIFICAR")
     * BUGS ---> (DONE) TRANSFERIR BACKUP PARA MENU LATERAL
     * BUGS ---> (DONE) EDITAR ENTRADAS, ELABORAR NOVO LAYOUT
     * BUGS ---> REFORMULAÇÃO DO BANCO DE DADOS: REUTILIZAÇÃO DE PRIMARY KEY COM BACKEND (VER LINK NO CELULAR)
     * E NOVO PARAMETRO "STATUS" PARA A ENTITY "REGISTRO"
     * BUGS ---> OTIMIZAR COMUNICAÇÃO DA SINCRONIZAÇÃO DE DADOS COM BACKEND
     */
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar.setTitle(R.string.activity_title_meus_registros)
        setSupportActionBar(toolbar)
        registerGlobalUIMessage()
        
        
        val toggle = DrawerToggle(this, drawer_layout, toolbar)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        
        drawer_navigation_view.setNavigationItemSelectedListener(this)
        onNavigationItemSelected(drawer_navigation_view.menu.getItem(0))
        
    }
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_drawer_movimentacao -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_main, MovimentacaoFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
            
                toolbar.setTitle(R.string.activity_title_meus_registros)
            }
            R.id.menu_drawer_entradas -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_main, EntradasFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
            
                toolbar.setTitle(R.string.activity_title_entradas)
            }
            R.id.menu_drawer_resumo -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_main, ResumoFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
    
                toolbar.setTitle(R.string.activity_title_resumos)
            }
            R.id.menu_drawer_dados -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_main, DadosBackupFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
        
                toolbar.setTitle(R.string.activity_title_dados_backup)
            }
        }
    
        searchMenuItem?.isVisible = item.itemId == R.id.menu_drawer_movimentacao
        addMenuItem?.isVisible = item.itemId == R.id.menu_drawer_movimentacao
        
        item.isChecked = true
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        searchMenuItem = menu.findItem(R.id.action_pesquisar)
        addMenuItem = menu.findItem(R.id.action_adicionar)
    
        val searchView = searchMenuItem!!.actionView as SearchView
        searchView.onActionViewCollapsed()
        searchView.queryHint = "O que você procura?"
        
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { return false }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                RegistroContext.textoPesquisa = newText
                Trigger.launch(Events.FiltrarRegistrosPelaDescricao())
                return true
            }
        })
        
        return true
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
    
    private fun registerGlobalUIMessage() {
        Trigger.watcher().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { t ->
                when (t) {
                    is Events.Toast -> toast(t.message)
                    is Events.Snack -> {
                        Snackbar.make(findViewById(android.R.id.content),
                            t.message,
                            Snackbar.LENGTH_LONG).show()
                    }
                }
            }.apply {  }
    }
    
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            val menuItem = drawer_navigation_view.menu.getItem(0)
            if (!menuItem.isChecked) {
                onNavigationItemSelected(menuItem)
                return
            }
            
            super.onBackPressed()
        }
    }
    
    override fun onStart() {
        super.onStart()
    
        setupFirebaseMessaging()
        Notification.notificar(this)
    }
    
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
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
