package br.com.myself.ui.financas.entradas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import br.com.myself.R
import br.com.myself.components.CalendarPickerEditText
import br.com.myself.domain.entity.Entrada
import br.com.myself.domain.repository.EntradaRepository
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.util.Async
import br.com.myself.util.CurrencyMask
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.setUpDimensions
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.dialog_criar_entrada.*
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import org.jetbrains.anko.support.v4.toast
import java.math.BigDecimal

class CriarEntradaDialog(
    private val entrada: Entrada? = null,
    private val repository: EntradaRepository
) : DialogFragment() {
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.dialog_criar_entrada, container, false)
    }
    
    override fun onStart() {
        super.onStart()
        
        dialog?.window?.setBackgroundDrawableResource(android.R.color.white)
        dialog?.setUpDimensions(widthPercent = (Utils.getScreenSize(requireContext()).x * 0.9).toInt())
        setUpView()
    }

    private fun setUpView() {
        
        if (entrada != null) { // Significa edição
            textinput_dialog_nova_entrada_descricao.setText(entrada.descricao)
            textinput_dialog_nova_entrada_valor.setText(Utils.formatCurrency(entrada.valor))
            calendar_picker_dialog_nova_entrada_data.setTime(entrada.data)
        }

        textinput_dialog_nova_entrada_valor.apply {
            addTextChangedListener(CurrencyMask(this))
            onFocusChange { v, hasFocus ->
                if (hasFocus) (v as TextInputEditText).setSelection(v.length()) }
        }
        
        calendar_picker_dialog_nova_entrada_data.setOnClickListener {
            (it as CalendarPickerEditText).showCalendar(childFragmentManager, null)
        }
        
        button_dialog_nova_entrada_salvar.setOnClickListener {

            val valor = textinput_dialog_nova_entrada_valor.text.toString()
            val fonte = textinput_dialog_nova_entrada_descricao.text.toString()

            if (fonte.isBlank()) {
                textinput_dialog_nova_entrada_descricao.requestFocus()
                Trigger.launch(Events.Toast("Campo Fonte vazio"))
            } else if (!isValorValido(valor)) {
                textinput_dialog_nova_entrada_valor.requestFocus()
                Trigger.launch(Events.Toast("Campo Valor inválido"))
            } else {

                val novaentrada = Entrada(
                    id = entrada?.id,
                    valor = Utils.unformatCurrency(valor).toDouble(),
                    descricao = fonte.trim(),
                    data = calendar_picker_dialog_nova_entrada_data.getTime()
                )
                
                Async.doInBackground({
                    repository.salvar(novaentrada)
                }, {
                    toast("Dados salvos!")
                    Trigger.launch(Events.UpdateEntradas)
                    dismiss()
                })
            }

        }
    }

    private fun isValorValido(valor: String): Boolean {
        return valor.isNotBlank() && Utils.unformatCurrency(valor).toBigDecimal() > BigDecimal.ZERO
    }
    
}
