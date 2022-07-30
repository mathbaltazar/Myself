package br.com.myself.ui.financas.registros

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import br.com.myself.R
import br.com.myself.components.CalendarPickerEditText
import br.com.myself.databinding.FragmentRegistroFormBinding
import br.com.myself.injectors.longSnackBar
import br.com.myself.injectors.provideRegistroRepo
import br.com.myself.util.CurrencyMask
import br.com.myself.util.Utils
import br.com.myself.viewmodel.RegistroFormViewModel
import com.google.android.material.textfield.TextInputEditText

class RegistroFormFragment : Fragment(R.layout.fragment_registro_form) {
    
    private var _binding: FragmentRegistroFormBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: RegistroFormViewModel by viewModels {
        RegistroFormViewModel.Factory(provideRegistroRepo()) }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegistroFormBinding.bind(view)
        
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
            
            viewModel.salvarRegistro(descricao, valor, outros, data)
            
            // Hide keyboard
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
        }
        
        binding.textinputValor.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) (v as TextInputEditText).setSelection(v.length())
        }
        
        registerObservers()
    }
    
    private fun registerObservers() {
        viewModel.registro.observe(viewLifecycleOwner) { registro ->
            if (registro != null) {
                binding.textinputDescricao.setText(registro.descricao)
                binding.textinputDescricao.setSelection(registro.descricao.length)
                binding.textinputValor.setText(Utils.formatCurrency(registro.valor))
                binding.calendarPickerData.setTime(registro.data)
                binding.textinputOutros.setText(registro.outros)
            }
        }
        
        viewModel.userMessage.observe(viewLifecycleOwner) { msg -> longSnackBar(msg) }
        
        viewModel.events.observe(viewLifecycleOwner) { event ->
            if (event is RegistroFormViewModel.Event.NavigateBack) {
                findNavController().popBackStack()
            }
        }
    }
    
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}