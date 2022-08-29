package br.com.myself.network

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import br.com.myself.data.BackendModelState
import br.com.myself.data.api.utils.BackendError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.RuntimeException

data class BackendIntegrationState(
    val sendingData: Boolean = false,
    val isUpToDate: Boolean = true,
    val onError: BackendError? = null,
)

abstract class NetworkDataIntegration {

    private var lifecycleOwner: LifecycleOwner? = null
    private var observer: ((BackendIntegrationState) -> Unit)? = null
    private val mState = MutableStateFlow(BackendIntegrationState())

    fun checkNetworkConnection(): Boolean {
        // TODO: Obter o estado da conexão
        return false
    }

    abstract fun doObserve(context: Context, lifecycleOwner: LifecycleOwner, observer: (BackendIntegrationState) -> Unit)

    protected fun <T : BackendModelState> synchronize(model: T): T = model.apply { isSynchronized = true }

    fun setLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner
        applyStateObserver()
    }

    private fun applyStateObserver() {
        with(lifecycleOwner ?: throw RuntimeException()) {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mState.collect {
                        observer?.invoke(it)
                    }
                }
            }
        }
    }



    fun setOnStateUpdated(observer: (BackendIntegrationState) -> Unit) {
        this.observer = observer
    }

    fun updateState(function: (BackendIntegrationState) -> BackendIntegrationState) = mState.update(function)


}