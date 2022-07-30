package br.com.myself.network

import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import br.com.myself.application.MyselfApplication
import br.com.myself.data.BackendModelState
import br.com.myself.data.api.BackendError
import br.com.myself.data.api.RegistroAPI
import br.com.myself.data.api.ServiceProvider
import br.com.myself.data.dao.RegistroDAO
import br.com.myself.data.model.Registro
import br.com.myself.transformer.ExpenseTransformer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BackendIntegrationState(
    val sendingData: Boolean = false,
    val isUpToDate: Boolean = true,
    val onError: BackendError? = null,
)

class BackendNetworkIntegration {
    
    companion object {
        private val expenseAPI: RegistroAPI = ServiceProvider.get(RegistroAPI::class.java)
    }
    
    private lateinit var expenseDAO: RegistroDAO
    private val mState = MutableStateFlow(BackendIntegrationState())
    
    
    private fun initDao(context: Context) {
        val app = context.applicationContext as MyselfApplication
        expenseDAO = app.database.getRegistroDAO()
    }
    
    fun state(viewLifecycleOwner: LifecycleOwner, observer: (BackendIntegrationState) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mState.collect {
                    observer(it)
                }
            }
        }
    }
    
    fun checkNetworkConnection(): Boolean {
        // TODO: Obter o estado da conexão
        return false
    }
    
    fun observeExpsense(context: Context, lifecycleOwner: LifecycleOwner) {
        initDao(context)
        
        expenseDAO.findAllToSync().observe(lifecycleOwner) { expenses ->
            if (!expenses.isNullOrEmpty()) {
                
                lifecycleOwner.lifecycleScope.launch {
                    try {
                        Log.d("Expense Network Integration", "Registers to send: $expenses")
                        mState.update { state -> state.copy(sendingData = true) }
                        
                        Log.d("Expense Network Integration", "Sending registers!")
                        val listDTO = expenses.map(ExpenseTransformer::toDTO)
                        val response =
                            expenseAPI.send(listDTO)
                
                        if (response.isSuccessful) {
                            Log.d("Network Integration | Expense","Response is successful")
                            
                            Log.d("Network Integration | Expense","Deleting synchronized")
                            val deleted = expenses.filter(Registro::isDeleted)
                            if (deleted.isNotEmpty()) {
                                expenseDAO.delete(deleted.toTypedArray())
                            }
                    
                            Log.d("Network Integration | Expense","Updating synchronized")
                            val update = expenses.filterNot(Registro::isDeleted)
                            expenseDAO.persist(update.map(::synchronize).toTypedArray())
                        }
                
                        mState.update { state ->
                            state.copy(sendingData = false, isUpToDate = response.isSuccessful)
                        }
                    } catch (e: BackendError) {
                        Log.d("Network Integration | Expense",
                            "Backend error: $e")
                        e.printStackTrace()
                        mState.update { state -> state.copy(sendingData = false, onError = e) }
                    }
                }
                
            }
            
        }
    }
    
    private fun <T : BackendModelState> synchronize(model: T): T {
        return model.apply { isSynchronized = true }
    }
}
    

