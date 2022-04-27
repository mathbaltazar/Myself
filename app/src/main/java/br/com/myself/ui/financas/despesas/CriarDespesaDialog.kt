package br.com.myself.ui.financas.despesas

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.ArrayAdapter
import br.com.myself.R
import br.com.myself.databinding.DialogCriarDespesaBinding
import br.com.myself.domain.entity.Despesa
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.util.CurrencyMask
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.setUpDimensions
import com.google.android.material.textfield.TextInputEditText

class CriarDespesaDialog(context: Context, private val onSave: (Dialog, Despesa) -> Unit) : Dialog(context) {
    
    private var vencimentoSelecionado: Int = 0
    private lateinit var binding: DialogCriarDespesaBinding
    
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.white)
    
        setUpDimensions(widthPercent = (Utils.getScreenSize(context).x * .85).toInt())
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = DialogCriarDespesaBinding.inflate(layoutInflater)
        setContentView(binding.root)
    
        binding.textinputValor.apply {
            addTextChangedListener(CurrencyMask(this))
        
            setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    (v as TextInputEditText).setSelection(v.length())
                }
            }
        }
    
        with(binding.dropdownVencimento) {
            setAdapter(obterVencimentoAdapter())
            setOnItemClickListener { _, _, position, _ ->
                vencimentoSelecionado = position
            }
            setText(adapter.getItem(vencimentoSelecionado).toString(), false)
        }
    
        binding.buttonSalvar.setOnClickListener {
            val nome = binding.textinputNome.text.toString()
            val valor = binding.textinputValor.text.toString()
        
            if (nome.isBlank()) {
                binding.textinputNome.requestFocus()
                Trigger.launch(Events.Toast("Nome inválido"))
                return@setOnClickListener
            }
            if (valor.isBlank()) {
                binding.textinputValor.requestFocus()
                Trigger.launch(Events.Toast("Valor inválido"))
                return@setOnClickListener
            }
            
            onSave(this, Despesa(
                nome = nome,
                valor = Utils.unformatCurrency(valor).toDouble(),
                diaVencimento = this.vencimentoSelecionado
            ))
            
        }
        
    }
    
    private fun obterVencimentoAdapter(): ArrayAdapter<String> {
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item)
        
        adapter.add("Sem vencimento")
        (1..28).forEach { dia -> adapter.add("$dia") }
        
        return adapter
    }
}
