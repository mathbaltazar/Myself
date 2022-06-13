package br.com.myself.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import br.com.myself.databinding.ActivityHomeBinding
import br.com.myself.notification.Notification
import br.com.myself.ui.crises.CrisesActivity
import br.com.myself.ui.financas.FinancasActivity
import com.google.firebase.messaging.FirebaseMessaging
import io.github.inflationx.viewpump.ViewPumpContextWrapper

class HomeActivity : AppCompatActivity() {
    
    private var firstUse: Boolean = true
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // FINANÇAS
        binding.buttonFinancas.setOnClickListener {
            startActivity(Intent(applicationContext, FinancasActivity::class.java))
        }
    
        // CRISES
        binding.buttonCrises.setOnClickListener {
            startActivity(Intent(applicationContext, CrisesActivity::class.java))
        }
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
            /* TODO val dialog = CriarRegistroDialog(this) DEEP LINK ??
            dialog.show()*/
        }
        firstUse = false
    }
    
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
}