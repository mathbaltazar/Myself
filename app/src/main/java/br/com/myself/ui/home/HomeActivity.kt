package br.com.myself.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import br.com.myself.ui.financas.FinancasActivity
import com.com.myself.R

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class HomeActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        
        // FINANÇAS
        findViewById<AppCompatButton>(R.id.button_home_financas).setOnClickListener {
            startActivity(Intent(applicationContext, FinancasActivity::class.java))
        }
        
    }
}