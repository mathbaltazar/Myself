package br.com.myself.ui.financas.state

import br.com.myself.ui.adapter.state.UIModelState
import br.com.myself.util.Utils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

data class RegistrosFragmentUIState(
    val totalGastos: String,
    val quantidadeGastos: String,
    val labelMesAnoSelecionado: String,
    val isEmpty: Boolean,
    val registros: List<UIModelState>
) {
    companion object {
        fun create(): RegistrosFragmentUIState {
            return RegistrosFragmentUIState(
                totalGastos = Utils.formatCurrency(0.0),
                quantidadeGastos = "0",
                labelMesAnoSelecionado = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM/yyyy")),
                isEmpty = true,
                registros = Collections.emptyList()
            )
        }
    }
}