package br.com.myself.ui.financas.state

data class RegistrosFragmentBehaviorState(
    val registroId: Long?
) {
    companion object {
        fun create(): RegistrosFragmentBehaviorState {
            return RegistrosFragmentBehaviorState(
                registroId = null
            )
        }
    }
}