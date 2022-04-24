package br.com.myself.ui.financas.despesas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import br.com.myself.R
import br.com.myself.components.CalendarPickerEditText
import br.com.myself.domain.entity.Despesa
import br.com.myself.util.CurrencyMask
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.setUpDimensions
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.dialog_registrar_despesa.*
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import java.math.BigDecimal
import java.util.*

class RegistrarDespesaDialog(
    private val despesa: Despesa,
    private val sugestoes: List<Double>,
    private val onRegister: (DialogFragment, Double, Calendar) -> Unit
) : DialogFragment() {
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.dialog_registrar_despesa, container, false)
    }
    
    override fun onStart() {
        super.onStart()
    
        dialog?.window?.setBackgroundDrawableResource(android.R.color.white)
        dialog?.setUpDimensions(widthPercent = (Utils.getScreenSize(requireContext()).x * .85).toInt())
        
        
        tv_dialog_registrar_despesa_nome.text = despesa.nome
        
        if (despesa.valor > 0.0) {
            tv_dialog_registrar_despesa_valor.text = Utils.formatCurrency(despesa.valor)
            tv_dialog_registrar_despesa_valor.visibility = View.VISIBLE
        }
        
        calendar_picker_dialog_registrar_despesa_data.setOnClickListener {
            (it as CalendarPickerEditText).showCalendar(childFragmentManager, null)
        }
        
        et_dialog_registrar_despesa_valor.apply {
            setText(Utils.formatCurrency(despesa.valor))
            addTextChangedListener(CurrencyMask(this))
            onFocusChange { v, hasFocus ->
                if (hasFocus) (v as TextInputEditText).setSelection(v.length())
            }
        }
        
        button_dialog_registrar_despesa_registrar.setOnClickListener {
            val valor = Utils.unformatCurrency(et_dialog_registrar_despesa_valor.text.toString()).toDouble()
            if (valor.toBigDecimal() <= BigDecimal.ZERO) {
                til_dialog_registrar_despesa_novo_valor.error = "Valor inválido"
                return@setOnClickListener
            }
            
            // Criação do registro a partir da despesa
            onRegister(this, valor, calendar_picker_dialog_registrar_despesa_data.getTime())
        }
        
        setUpSugestoes()
    }
    
    private fun setUpSugestoes() {
        sugestoes.sorted().forEach { valor ->
            val button = layoutInflater.inflate(R.layout.button_dialog_registrar_despesa_sugestao,
                flexbox_dialog_registrar_despesa_sugestoes_valor,
                false) as MaterialButton
            
            button.text = Utils.formatCurrency(valor)
            button.setOnClickListener {
                et_dialog_registrar_despesa_valor.setText(button.text)
            }
            
            flexbox_dialog_registrar_despesa_sugestoes_valor.addView(button)
        }
    }
    
}
