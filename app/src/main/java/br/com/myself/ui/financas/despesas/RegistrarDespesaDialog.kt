package br.com.myself.ui.financas.despesas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import br.com.myself.R
import br.com.myself.components.CalendarPickerEditText
import br.com.myself.databinding.DialogRegistrarDespesaBinding
import br.com.myself.model.entity.Despesa
import br.com.myself.util.CurrencyMask
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.setUpDimensions
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.math.BigDecimal
import java.util.*

class RegistrarDespesaDialog(
    private val despesa: Despesa,
    private val sugestoes: List<Double>,
    private val onRegister: (DialogFragment, Double, Calendar) -> Unit
) : DialogFragment() {
    
    private var _binding: DialogRegistrarDespesaBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        _binding = DialogRegistrarDespesaBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    
        binding.textviewNome.text = despesa.nome
    
        if (despesa.valor > 0.0) {
            binding.textviewValor.text = Utils.formatCurrency(despesa.valor)
            binding.textviewValor.visibility = View.VISIBLE
        }
    
        binding.calendarPickerData.setOnClickListener {
            (it as CalendarPickerEditText).showCalendar(childFragmentManager, null)
        }
    
        binding.textinputValor.apply {
            setText(Utils.formatCurrency(despesa.valor))
            addTextChangedListener(CurrencyMask(this))
            setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) (v as TextInputEditText).setSelection(v.length())
            }
        }
    
        binding.buttonRegistrar.setOnClickListener {
            val valor = Utils.unformatCurrency(binding.textinputValor.text.toString()).toDouble()
            if (valor.toBigDecimal() <= BigDecimal.ZERO) {
                binding.textinputLayoutValor.error = "Valor inválido"
                return@setOnClickListener
            }
        
            // Criação do registro a partir da despesa
            onRegister(this, valor, binding.calendarPickerData.getTime())
        }
    
        setUpSugestoes()
    }
    
    override fun onStart() {
        super.onStart()
    
        dialog?.window?.setBackgroundDrawableResource(android.R.color.white)
        dialog?.setUpDimensions(widthPercent = (Utils.getScreenSize(requireContext()).x * .85).toInt())
    }
    
    private fun setUpSugestoes() {
        sugestoes.sorted().forEach { valor ->
            val button = layoutInflater.inflate(R.layout.button_dialog_registrar_despesa_sugestao,
                binding.flexboxSugestoes,
                false) as MaterialButton
            
            button.text = Utils.formatCurrency(valor)
            button.setOnClickListener {
                binding.textinputValor.setText(button.text)
            }
            
            binding.flexboxSugestoes.addView(button)
        }
    }
    
}
