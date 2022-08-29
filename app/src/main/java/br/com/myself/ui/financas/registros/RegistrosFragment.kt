package br.com.myself.ui.financas.registros

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.R
import br.com.myself.databinding.FragmentRegistrosBinding
import br.com.myself.injectors.provideRegistroRepo
import br.com.myself.ui.adapter.RegistroAdapter
import br.com.myself.ui.confirmation.ConfirmationDialogDirections
import br.com.myself.ui.financas.state.RegistrosFragmentUIState
import br.com.myself.util.KEY_IS_REGISTRO_DETAILS_SHOWN
import br.com.myself.util.KEY_REGISTRO_ID
import br.com.myself.util.observe
import br.com.myself.viewmodel.RegistrosViewModel

class RegistrosFragment : Fragment(R.layout.fragment_registros) {

    private val viewModel: RegistrosViewModel by viewModels { RegistrosViewModel.Factory(provideRegistroRepo()) }

    private var _binding: FragmentRegistrosBinding? = null
    private val binding  get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegistrosBinding.bind(view)

        // MONTH PAGE
        binding.buttonPageMesAnterior.setOnClickListener {
            viewModel.mesAnterior()
        }
        binding.buttonPageProximoMes.setOnClickListener {
            viewModel.proximoMes()
        }

        // AÇÃO BOTÃO PESQUISAR
        binding.buttonPesquisar.setOnClickListener {
            val direction = RegistrosFragmentDirections.toPesquisarRegistroDest()
            findNavController().navigate(direction)
        }

        // AÇÃO BOTÃO ADICIONAR (+)
        binding.buttonAdicionar.setOnClickListener {
            val direction = RegistrosFragmentDirections.toRegistroFormDest()
            findNavController().navigate(direction)
        }

        // RECYCLER VIEW
        binding.recyclerviewRegistros.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerviewRegistros.adapter = RegistroAdapter()

        registerObservers()
    }

    private fun registerObservers() {
        viewModel.uiBehaviorState.observe(viewLifecycleOwner) { currentState ->
            currentState.registroId?.let {
                findNavController().navigate(R.id.to_card_detalhes_registro_dest,
                        bundleOf(KEY_REGISTRO_ID to it))
                setFragmentResultListener(KEY_IS_REGISTRO_DETAILS_SHOWN) { _, bdl ->
                    if (bdl.getBoolean(KEY_IS_REGISTRO_DETAILS_SHOWN, false)) {
                        viewModel.detailsShown()
                    }
                }
                clearFragmentResult(KEY_IS_REGISTRO_DETAILS_SHOWN)
            }
        }

        viewModel.observeRegistroUIState(viewLifecycleOwner, ::setupView)

        viewModel.expenseDataIntegration.doObserve(requireContext(), viewLifecycleOwner) { state ->
            binding.progressIncicator.root.visibility = if (state.sendingData) View.VISIBLE else View.GONE

            state.onError?.let {
                ConfirmationDialogDirections.toConfirm("Network", it.stackTraceToString())
                findNavController().navigate(R.id.confirmation_dest)
            }
        }
    }

    private fun setupView(state: RegistrosFragmentUIState) {
        binding.textViewTotalMes.text = state.totalGastos
        binding.textViewQuantidadeRegistros.text = state.quantidadeGastos
        binding.textViewMesAnoSelecionado.text = state.labelMesAnoSelecionado
        binding.textviewNenhumRegistroEncontrado.visibility = if (state.isEmpty) View.VISIBLE else View.GONE

        (binding.recyclerviewRegistros.adapter as RegistroAdapter).submitList(state.registros)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}