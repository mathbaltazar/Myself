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
                    observe(lifecycleOwner, expenses, expenseDAO)
                }
            }
        }

    }

    private fun observe(
        lifecycleOwner: LifecycleOwner,
        expenses: List<Registro>?,
        dao: RegistroDAO) {
        if (!expenses.isNullOrEmpty()) {
            lifecycleOwner.lifecycleScope.launch {
                try {
                    Log.d("Expense Network Integration", "Registers to send: $expenses")
                    updateState { state -> state.copy(sendingData = true, onError = null) }

                    Log.d("Expense Network Integration", "Sending registers!")
                    val response =
                            expenseAPI.send(expenses.map(ExpenseTransformer::toDTO))

                    if (response.isSuccessful) {
                        Log.d("Network Integration | Expense","Response is successful")

                        Log.d("Network Integration | Expense","Updating synchronized")
                        val update = expenses.filterNot(Registro::isDeleted)
                        dao.persist(update.map(::synchronize).toTypedArray())

                        Log.d("Network Integration | Expense","Deleting synchronized")
                        dao.clearDeleted()
                    }

                    updateState { state ->
                        state.copy(sendingData = false, isUpToDate = response.isSuccessful)
                    }
                } catch (e: BackendError) {
                    Log.d("Network Integration | Expense",
                            "Error: $e")
                    updateState { state -> state.copy(sendingData = false, onError = e) }
                }
            }

        }
    }
}
    

