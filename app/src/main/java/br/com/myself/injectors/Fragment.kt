package br.com.myself.injectors

import android.view.View
import androidx.fragment.app.Fragment
import br.com.myself.application.Application
import br.com.myself.repository.EntradaRepository
import br.com.myself.services.EntradaService
import br.com.myself.services.ServiceProvider
import com.google.android.material.snackbar.Snackbar

fun Fragment.activityContentView(): View {
    return requireActivity().findViewById(android.R.id.content)
}

fun Fragment.longSnackBar(s: String): Snackbar {
    return Snackbar.make(
        activityContentView(),
        s,
        Snackbar.LENGTH_LONG
    )
}

fun Fragment.getApplicationContext(): Application {
    return requireActivity().applicationContext as Application
}

fun <T> Fragment.provideRepo(repositoryClass: Class<T>): T {
    if (repositoryClass == EntradaRepository::class.java) {
        return EntradaRepository(
            getApplicationContext().database.getEntradaDAO(),
            ServiceProvider.get(EntradaService::class.java)
        ) as T
    }
    throw IllegalArgumentException("Unknown repository")
}