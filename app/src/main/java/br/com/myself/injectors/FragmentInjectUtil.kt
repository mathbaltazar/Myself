package br.com.myself.injectors

import android.view.View
import androidx.fragment.app.Fragment
import br.com.myself.application.MyselfApplication
import br.com.myself.data.api.EntradaAPI
import br.com.myself.data.api.utils.ApiProvider
import br.com.myself.repository.EntradaRepository
import br.com.myself.repository.RegistroRepository
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

// Return activity viewport
private fun Fragment.activityContentView(): View {
    return requireActivity().findViewById(android.R.id.content)
}

fun Fragment.longSnackBar(s: String, dismissCallback: () -> Unit = {}) {
    Snackbar.make(activityContentView(), s, Snackbar.LENGTH_LONG)
        .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                dismissCallback()
            }
        })
        .show()
}

fun Fragment.getApplicationContext(): MyselfApplication {
    return requireActivity().applicationContext as MyselfApplication
}

fun Fragment.provideEntradaRepo(): EntradaRepository =
    EntradaRepository(getApplicationContext().database.getEntradaDAO(),
        ApiProvider.get(EntradaAPI::class.java))

fun Fragment.provideRegistroRepo(): RegistroRepository =
    RegistroRepository(getApplicationContext().database.getRegistroDAO())

/*TODO fun Fragment.provideDespesaRepo(): DespesaRepository =
    DespesaRepository(getApplicationContext().database.getDespesaDAO(),
        ServiceProvider.get(DespesaAPI::class.java))
       fun Fragment.provideCriseRepo(): CriseRepository =
    CriseRepository(getApplicationContext().database.getCriseDAO(),
        ServiceProvider.get(CriseAPI::class.java))*/
