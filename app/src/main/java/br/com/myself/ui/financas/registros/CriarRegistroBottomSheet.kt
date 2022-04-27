package br.com.myself.ui.financas.registros

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import br.com.myself.components.CalendarPickerEditText
import br.com.myself.databinding.BottomSheetRegistroBinding
import br.com.myself.domain.entity.Registro
import br.com.myself.util.CurrencyMask
import br.com.myself.util.Utils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import org.jetbrains.anko.support.v4.toast
import java.math.BigDecimal

class CriarRegistroBottomSheet(
    private val registro: Registro? = null,
    private val onSave: (DialogFragment, Registro) -> Unit
) : BottomSheetDialogFragment() {
    
    private var _binding: BottomSheetRegistroBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetRegistroBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.textinputValor.apply {
            addTextChangedListener(CurrencyMask(this))
        }
        binding.calendarPickerData.setOnClickListener {
            (it as CalendarPickerEditText).showCalendar(childFragmentManager, null)
        }
        
        binding.buttonRegistrar.setOnClickListener {
            val descricao = binding.textinputDescricao.text.toString()
            val valor = binding.textinputValor.text.toString()
            val outros = binding.textinputOutros.text.toString()
            val data = binding.calendarPickerData.getTime()
            
            if (descricao.isBlank()) {
                binding.textinputDescricao.requestFocus()
                toast("Campo Descrição vazio")
            } else if (!isValorValido(valor)) {
                binding.textinputValor.requestFocus()
                toast("Campo Valor inválido")
            } else {
                onSave(this, Registro(
                    id = registro?.id,
                    descricao = descricao.trim(),
                    valor = Utils.unformatCurrency(valor).toDouble(),
                    outros = outros.trim(),
                    data = data
                ))
            }
        }
        
        binding.textinputValor.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) (v as TextInputEditText).setSelection(v.length())
        }
        
        // Caso registro não for NULL, significa uma edição
        if (registro != null) {
            binding.textinputDescricao.setText(registro.descricao)
            binding.textinputDescricao.setSelection(registro.descricao.length)
            binding.textinputValor.setText(Utils.formatCurrency(registro.valor))
            binding.calendarPickerData.setTime(registro.data)
            binding.textinputOutros.setText(registro.outros)
        }
    }
    
    private fun isValorValido(valor: String): Boolean {
        return valor.isNotBlank() && Utils.unformatCurrency(valor).toBigDecimal() > BigDecimal.ZERO
    }
    
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}