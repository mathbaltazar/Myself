package br.com.myself.ui.financas.entradas

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import br.com.myself.R
import br.com.myself.repository.EntradaRepository
import br.com.myself.databinding.DialogCardEntradaDetalhesBinding
import br.com.myself.ui.confirmation.ConfirmationDialogDirections
import br.com.myself.injectors.longSnackBar
import br.com.myself.injectors.provideRepo
import br.com.myself.util.REQUEST_KEY_CARD_DETAILS_DELETE
import br.com.myself.util.REQUEST_KEY_ENTRADA_DETAILS_CLOSE
import br.com.myself.util.FRAGMENT_RESULT_IS_ENTRADA_EDITING
import br.com.myself.util.Utils
import br.com.myself.util.Utils.Companion.formattedDate
import br.com.myself.viewmodel.DetalhesEntradaViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class DetalhesEntradaBottomSheet : BottomSheetDialogFragment() {
    
    private val viewModel: DetalhesEntradaViewModel
        by viewModels { DetalhesEntradaViewModel.Factory(provideRepo(EntradaRepository::class.java)) }
    private var isEditing: Boolean = false
    
    private var _binding: DialogCardEntradaDetalhesBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogCardEntradaDetalhesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    
        binding.buttonEditar.setOnClickListener {
            viewModel.onEditarClick()
        }
    
        binding.buttonExcluir.setOnClickListener {
            viewModel.onDeleteClick()
        }
        
        subscribeObservers()
        
        setFragmentResultListener(REQUEST_KEY_CARD_DETAILS_DELETE) { _, bundle ->
            val resultOk = bundle.getBoolean("result")
            if (resultOk) {
                viewModel.excluirEFechar()
            }
        }
    }
    
    @SuppressLint("SetTextI18n")
    private fun subscribeObservers() {
        
        viewModel.selectedEntrada.observe(viewLifecycleOwner) { entrada ->
            if (entrada != null) { // Retorno é null, quando deletado
                binding.textviewDescricao.text = entrada.descricao
                binding.textviewValor.text = Utils.formatCurrency(entrada.valor)
                binding.textviewData.text = entrada.data.formattedDate()
                if (entrada.isSynchronized) {
                    binding.textviewSincronizado.text = "Sincronizado! "
                    binding.textviewSincronizado.setCheckMarkDrawable(R.drawable.ic_check)
                    binding.textviewSincronizado.checkMarkTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.colorAccent, null))
                }
            }
        }
        
        viewModel.eventStreamLiveData.observe(viewLifecycleOwner) { event ->
            when (event) {
                is DetalhesEntradaViewModel.Events.Edit -> {
                    isEditing = true
                    val direction = DetalhesEntradaBottomSheetDirections.toEntradaFormDest(event.entrada)
                    findNavController().navigate(direction)
                }
                is DetalhesEntradaViewModel.Events.Delete -> {
                    val request = REQUEST_KEY_CARD_DETAILS_DELETE
                    val msg = "Excluir entrada ?"
                    val direction = ConfirmationDialogDirections.toConfirm(request, msg)
                    findNavController().navigate(direction)
                }
                is DetalhesEntradaViewModel.Events.Message -> longSnackBar(event.text.toString()).show()
                is DetalhesEntradaViewModel.Events.CloseDetails -> findNavController().popBackStack()
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressIndicator.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }
    
    override fun dismiss() {
        setFragmentResult(REQUEST_KEY_ENTRADA_DETAILS_CLOSE, bundleOf(
            FRAGMENT_RESULT_IS_ENTRADA_EDITING to isEditing )
        )
        super.dismiss()
    }
    
    
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    
}
