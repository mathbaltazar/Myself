package br.com.myself.ui.financas

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import br.com.myself.R
import br.com.myself.databinding.ActivityFinancasBinding
import io.github.inflationx.viewpump.ViewPumpContextWrapper

class FinancasActivity : AppCompatActivity() {
    
    private val binding: ActivityFinancasBinding by lazy { ActivityFinancasBinding.inflate(layoutInflater) }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        
        val navController = (binding.navHostContainer.getFragment() as NavHostFragment).navController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
        val config = AppBarConfiguration(
            topLevelDestinationIds = setOf(R.id.registros_dest, R.id.despesas_dest,R.id.entradas_dest),
            fallbackOnNavigateUpListener = ::onSupportNavigateUp
        )
        NavigationUI.setupWithNavController(binding.toolbar, navController, config)
    }
    
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
}
