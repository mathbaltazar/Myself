package br.com.myself.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import br.com.myself.domain.entity.Crise
import br.com.myself.domain.repository.CriseRepository
import br.com.myself.util.Async

class CrisesActivityViewModel(application: Application): AndroidViewModel(application) {
    private val repository by lazy { CriseRepository(application) }
    
    val crises: LiveData<List<Crise>> = repository.getTodasCrises()
    
    fun excluirCrise(crise: Crise, onDeleted: () -> Unit) {
        Async.doInBackground({ repository.excluir(crise) }, { onDeleted() })
    }
    
    fun salvarCrise(novacrise: Crise, onSaved: () -> Unit) {
        Async.doInBackground({ repository.salvar(novacrise) }, { onSaved() })
    }
    
}
