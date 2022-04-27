package br.com.myself.ui.financas.entradas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import br.com.myself.databinding.DialogCriarEntradaBinding
import br.com.myself.domain.entity.Entrada
import br.com.myself.util.CurrencyMask
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.setUpDimensions
import com.google.android.material.textfield.TextInputEditText
import org.jetbrains.anko.support.v4.toast
import java.math.BigDecimal

class CriarEntradaDialog(
    private val entrada: Entrada? = null,
    private val onSave: (DialogFragment, Entrada) -> Unit
) : DialogFragment() {
    
    private var _binding: DialogCriarEntradaBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        _binding = DialogCriarEntradaBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    
    override fun onStart() {
        super.onStart()
        
        dialog?.window?.setBackgroundDrawableResource(android.R.color.white)
        dialog?.setUpDimensions(widthPercent = (Utils.getScreenSize(requireContext()).x * 0.9).toInt())
        setUpView()
    }

    private fun setUpView() {
        
        if (entrada != null) { // Significa edição
            binding.textinputFonte.setText(entrada.descricao)
            binding.textinputValor.setText(Utils.formatCurrency(entrada.valor))
            binding.calendarPickerData.setTime(entrada.data)
        }

        binding.textinputValor.apply {
            addTextChangedListener(CurrencyMask(this))
            setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) (v as TextInputEditText).setSelection(v.length()) }
        }
        
        binding.calendarPickerData.apply {
            setOnClickListener { showCalendar(childFragmentManager, null) }
        }
    
        binding.buttonSalvar.setOnClickListener {
            val valor = binding.textinputValor.text.toString()
            val fonte = binding.textinputFonte.text.toString()
    
            if (fonte.isBlank()) {
                binding.textinputFonte.requestFocus()
                toast("Campo Fonte vazio")
            } else if (!isValorValido(valor)) {
                binding.textinputValor.requestFocus()
                toast("Campo Valor inválido")
            } else {
                onSave(this, Entrada(
                    id = entrada?.id,
                    valor = Utils.unformatCurrency(valor).toDouble(),
                    descricao = fonte.trim(),
                    data = binding.calendarPickerData.getTime()
                ))
            }
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
