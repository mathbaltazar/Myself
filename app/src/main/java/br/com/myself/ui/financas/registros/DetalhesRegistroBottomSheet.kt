package br.com.myself.ui.financas.registros

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.clearFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import br.com.myself.databinding.BottomSheetDetalhesRegistroBinding
import br.com.myself.injectors.provideRegistroRepo
import br.com.myself.ui.confirmation.CONFIRMATION_DIALOG_RESULT
import br.com.myself.util.REQUEST_KEY_CARD_DETAILS_DELETE
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.formattedDate
import br.com.myself.viewmodel.DetalhesRegistroViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DetalhesRegistroBottomSheet : BottomSheetDialogFragment() {
    
    private val viewModel: DetalhesRegistroViewModel by viewModels {
        DetalhesRegistroViewModel.Factory(provideRegistroRepo())
    }
    
    private var _binding: BottomSheetDetalhesRegistroBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDetalhesRegistroBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.buttonEditar.setOnClickListener {
            viewModel.onEditButtonClick()
        }
        binding.buttonExcluir.setOnClickListener {
            viewModel.onDeleteButtonClick()
        }
    
        subscribeObservers()
    }
    
    private fun subscribeObservers() {
        viewModel.registro.observe(viewLifecycleOwner) { registro ->
            if (registro != null) {
                binding.textviewDescricao.text = registro.descricao
                binding.textviewData.text = registro.data.formattedDate()
                binding.textviewValor.text = Utils.formatCurrency(registro.valor)
                binding.textviewOutros.text = registro.outros
            
                binding.textviewReferenciaDespesa.visibility =
                    if (registro.despesa_id != null) View.VISIBLE else View.GONE
            }
        }
    
        viewModel.eventStream.observe(viewLifecycleOwner) { event ->
            when (event) {
                is DetalhesRegistroViewModel.Event.OnEdit -> {
                    val title = "Editar Registro"
                    val direction = RegistrosFragmentDirections.toRegistroFormDest(event.registro, title)
                    findNavController().navigate(direction)
                }
                is DetalhesRegistroViewModel.Event.OnDelete -> {
                    val requestKey = REQUEST_KEY_CARD_DETAILS_DELETE
                    val msg = "Excluir o Registro ?"
                    val direction = DetalhesRegistroBottomSheetDirections.toConfirm(requestKey, msg)
                    findNavController().navigate(direction)
                    setFragmentResultListener(requestKey) { _, bdl ->
                        val confirm = bdl.getBoolean(CONFIRMATION_DIALOG_RESULT, false)
                        viewModel.delete(confirm)
                        clearFragmentResult(requestKey)
                    }
                }
            }
        }
    }
    
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    
}
