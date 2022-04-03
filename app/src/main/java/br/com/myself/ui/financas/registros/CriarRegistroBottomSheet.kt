package br.com.myself.ui.financas.registros

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.myself.R
import br.com.myself.components.CalendarPickerEditText
import br.com.myself.model.entity.Registro
import br.com.myself.model.repository.RegistroRepository
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.util.Async
import br.com.myself.util.CurrencyMask
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.getCalendar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.bottom_sheet_registro.view.*
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import org.jetbrains.anko.support.v4.toast
import java.math.BigDecimal

class CriarRegistroBottomSheet(
    private val registro: Registro? = null,
    private val repository: RegistroRepository
) : BottomSheetDialogFragment() {
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_registro, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        view.textinput_bottom_sheet_registro_valor.apply {
            addTextChangedListener(CurrencyMask(this))
        }
        
        view.calendar_picker_bottom_sheet_registro_data.setOnClickListener {
            (it as CalendarPickerEditText).showCalendar(childFragmentManager, null)
        }
        
        view.button_bottom_sheet_registro_adicionar.setOnClickListener {
            
            val descricao = view.textinput_bottom_sheet_registro_descricao.text.toString()
            val valor = view.textinput_bottom_sheet_registro_valor.text.toString()
            val outros = view.textinput_bottom_sheet_registro_outros.text.toString()
            val data = view.calendar_picker_bottom_sheet_registro_data.getTime()
            
            if (descricao.isBlank()) {
                view.textinput_bottom_sheet_registro_descricao.requestFocus()
                toast("Campo Descrição vazio")
            } else if (!isValorValido(valor)) {
                view.textinput_bottom_sheet_registro_valor.requestFocus()
                toast("Campo Valor inválido")
            } else {
                
                val novoregistro = Registro(
                    id = registro?.id,
                    descricao = descricao.trim(),
                    valor = Utils.unformatCurrency(valor).toDouble(),
                    outros = outros.trim(),
                    data = data.getCalendar()
                )
    
                Async.doInBackground({
                    repository.salvarRegistro(novoregistro)
                }, {
                    toast("Dados salvos!")
                    Trigger.launch(Events.UpdateRegistros, Events.AtualizarDetalhesRegistro(novoregistro))
        
                    dismiss()
                })
            }
        }
        
        view.textinput_bottom_sheet_registro_valor.onFocusChange { v, hasFocus ->
            if (hasFocus) (v as TextInputEditText).setSelection(v.length())
        }
        
        // Caso registro não for NULL, significa uma edição
        if (registro != null) {
            view.textinput_bottom_sheet_registro_descricao.setText(registro.descricao)
            view.textinput_bottom_sheet_registro_descricao.setSelection(registro.descricao.length)
            
            view.textinput_bottom_sheet_registro_valor.setText(Utils.formatCurrency(registro.valor))
            view.calendar_picker_bottom_sheet_registro_data.setTime(registro.data.timeInMillis)
        }
    }
    
    private fun isValorValido(valor: String): Boolean {
        return valor.isNotBlank() && Utils.unformatCurrency(valor).toBigDecimal() > BigDecimal.ZERO
    }
}