package br.com.myself.network

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import br.com.myself.application.MyselfApplication
import br.com.myself.data.api.ExpenseAPI
import br.com.myself.data.api.utils.ApiProvider
import br.com.myself.data.api.utils.BackendError
import br.com.myself.data.dao.RegistroDAO
import br.com.myself.data.model.Registro
import br.com.myself.transformer.ExpenseTransformer
import kotlinx.coroutines.launch

class ExpenseIntegration : NetworkDataIntegration() {
    private val expenseAPI: ExpenseAPI = ApiProvider.get(ExpenseAPI::class.java)
    
    override fun doObserve(context: Context, lifecycleOwner: LifecycleOwner, observer: (BackendIntegrationState) -> Unit) {
        setLifecycleOwner(lifecycleOwner)
        setOnStateUpdated(observer)

        with(context.applicationContext as MyselfApplication) {
            database.getRegistroDAO().let { expenseDAO ->
                // Start observing Expense data
                expenseDAO.findAllToSync().observe(lifecycleOwner) { expenses ->
                    if (!expenses.isNullOrEmpty()) {
                        lifecycleOwner.lifecycleScope.launch {
                            sync(expenses, expenseDAO)
                        }
                    }
                }
            }
        }

    }

    private suspend fun sync(expenses: List<Registro>, dao: RegistroDAO) {
        try {
            Log.d("Expense Network Integration", "Registers to send: $expenses")
            updateState { state -> state.copy(sendingData = true, onError = null) }

            val toDelete = expenses.filter(Registro::isDeleted)
            if (toDelete.isNotEmpty()) {
                expenseAPI.delete(toDelete.map(ExpenseTransformer::toDTO))
                    .let { response ->
                        if (response.isSuccessful) {
                            Log.d("Network Integration | Expense", "Deleting synchronized")
                            dao.clearDeleted()
                        }
                    }
            }


            val toUpdate = expenses.filterNot(Registro::isDeleted)
            if (toUpdate.isNotEmpty()) {
                expenseAPI.send(expenses.map(ExpenseTransformer::toDTO))
                    .let { response ->
                        if (response.isSuccessful) {
                            Log.d("Network Integration | Expense", "Updating synchronized")
                            dao.persist(toUpdate.map(::synchronize).toTypedArray())
                        }
                    }
            }

            updateState { state -> state.copy(sendingData = false, isUpToDate = true) }
        } catch (e: BackendError) {
            Log.d("Network Integration | Expense", "Error: $e")
            updateState { state -> state.copy(sendingData = false, onError = e, isUpToDate = false) }
        }

    }
}
    

