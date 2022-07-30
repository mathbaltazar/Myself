package br.com.myself.ui.financas.entradas

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import br.com.myself.R
import br.com.myself.databinding.FragmentEntradaFormBinding
import br.com.myself.injectors.provideEntradaRepo
import br.com.myself.util.CurrencyMask
import br.com.myself.util.Utils
import br.com.myself.viewmodel.EntradaFormViewModel
import com.google.android.material.textfield.TextInputEditText

class EntradaFormFragment : Fragment(R.layout.fragment_entrada_form) {
    
    private var _binding: FragmentEntradaFormBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: EntradaFormViewModel
        by viewModels { EntradaFormViewModel.Factory(provideEntradaRepo()) }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEntradaFormBinding.bind(view)
        
        setUpView()
        subscribeObservers()
        registerFieldsToViewModel()
        viewModel.entrada.observe(viewLifecycleOwner) { entrada ->
            if (entrada != null) { // Significa edição
                binding.textinputDescricao.setText(entrada.descricao)
                binding.textinputValor.setText(Utils.formatCurrency(entrada.valor))
                binding.calendarPickerData.setTime(entrada.data)
                requireActivity().actionBar?.title = "Editar Entrada"
            }
        }
    }
    
    private fun subscribeObservers() {
        viewModel.userMessage.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
        
        viewModel.events.observe(viewLifecycleOwner) { event ->
            (event is EntradaFormViewModel.Events.Saving).also { saving ->
                binding.buttonSalvar.isClickable = !saving
                binding.textinputDescricao.isEnabled = !saving
                binding.textinputValor.isEnabled = !saving
                binding.calendarPickerData.isEnabled = !saving
                binding.progressIncicator.visibility = if (saving) View.VISIBLE else View.GONE
            }
    
            if (event is EntradaFormViewModel.Events.SavingSuccessful) findNavController().popBackStack()
            
        }
    }
    
    private fun registerFieldsToViewModel() {
        binding.textinputDescricao.apply { addTextChangedListener {
                viewModel.setDescricao(text.toString())
            }
        }
        
        binding.textinputValor.apply { addTextChangedListener {
                val valor = Utils.unformatCurrency(text.toString()).toDouble()
                viewModel.setValor(valor)
            }
        }
        
        binding.calendarPickerData.setOnDateSelected { calendar ->
            viewModel.setData(calendar)
        }
    }
    
    private fun setUpView() {
        binding.textinputValor.apply {
            addTextChangedListener(CurrencyMask(this))
            setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) (v as TextInputEditText).setSelection(v.length()) }
        }
        
        binding.calendarPickerData.bindFragmentManager(childFragmentManager)
    
        binding.buttonSalvar.setOnClickListener {
            viewModel.salvarEntrada()
            // Esconder teclado
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
        }
        
    }
    
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
