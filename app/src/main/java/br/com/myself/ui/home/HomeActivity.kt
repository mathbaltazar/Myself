package br.com.myself.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import br.com.myself.R
import br.com.myself.model.entity.Registro
import br.com.myself.model.repository.RegistroRepository
import br.com.myself.notification.Notification
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.ui.crises.CrisesActivity
import br.com.myself.ui.financas.FinancasActivity
import br.com.myself.util.Async
import br.com.myself.util.Utils
import com.google.firebase.messaging.FirebaseMessaging
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.toast
import kotlin.math.ceil

class HomeActivity : AppCompatActivity() {
    
    private var firstUse: Boolean = true
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        
        // FINANÇAS
        button_home_financas.setOnClickListener {
            startActivity(Intent(applicationContext, FinancasActivity::class.java))
        }
    
        // CRISES
        button_home_crises.setOnClickListener {
            startActivity(Intent(applicationContext, CrisesActivity::class.java))
        }
        
        registerGlobalToast()
        
        // TESTE 2: Instanciando Repository
        Async.doInBackground {
            val repository = RegistroRepository(applicationContext)
            repository.salvarRegistro(Registro(descricao = "Teste ${4}", data = Utils.getCalendar(), valor = ceil(Math.random() + 1)))
        }
        
    }
    
    private fun registerGlobalToast() {
        Trigger.watcher().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { t ->
                when (t) {
                    is Events.Toast -> toast(t.message)
                }
            }.apply {  }
    }
    
    private fun setupFirebaseMessaging() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@addOnCompleteListener
                }
    
                task.result?.let { Log.w("FCM Token Registration", it) }
            }
    }
    
    override fun onStart() {
        super.onStart()
        
        setupFirebaseMessaging()
        Notification.notificar(this)
    }
    
    override fun onResume() {
        super.onResume()
    
        if (intent.action == "abrir_adicionar_gasto" && firstUse) {
            /* TODO val dialog = CriarRegistroDialog(this)
            dialog.show()*/
        }
        firstUse = false
    }
    
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
}