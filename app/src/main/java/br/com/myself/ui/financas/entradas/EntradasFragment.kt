package br.com.myself.ui.financas.entradas

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.R
import br.com.myself.repository.EntradaRepository
import br.com.myself.databinding.FragmentEntradasBinding
import br.com.myself.ui.adapter.EntradaAdapter
import br.com.myself.injectors.longSnackBar
import br.com.myself.injectors.provideRepo
import br.com.myself.util.FRAGMENT_RESULT_IS_ENTRADA_EDITING
import br.com.myself.util.REQUEST_KEY_ENTRADA_DETAILS_CLOSE
import br.com.myself.viewmodel.EntradaViewModel

class EntradasFragment : Fragment(R.layout.fragment_entradas) {
    
    private var _binding: FragmentEntradasBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: EntradaViewModel
        by viewModels { EntradaViewModel.Factory(provideRepo(EntradaRepository::class.java)) }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEntradasBinding.bind(view)
        
        setUpAdapter()
        subscribeObservers()
        subscribeFragmentResultListeners()
        
        binding.buttonVoltarAno.setOnClickListener {
            viewModel.voltarAno()
        }
        
        binding.buttonAvancarAno.setOnClickListener {
            viewModel.avancarAno()
        }
        
        binding.buttonAdicionar.setOnClickListener {
            val direction = EntradasFragmentDirections.toEntradaFormDest()
            findNavController().navigate(direction)
        }
    }
    
    private fun subscribeObservers() {
        viewModel.entradaEventsLiveData.observe(viewLifecycleOwner) { event ->
            when (event) {
                is EntradaViewModel.Events.ShowCardDetails -> {
                    val direction = EntradasFragmentDirections.toEntradaDetalhesDest(event.itemId)
                    findNavController().navigate(direction)
                }
                is EntradaViewModel.Events.Message -> longSnackBar(event.message.toString()).show()
                is EntradaViewModel.Events.HideCardDetails -> {}
            }
        }
        
        viewModel.entradas.observe(viewLifecycleOwner) {
            (binding.recyclerView.adapter as EntradaAdapter).submitData(lifecycle, it)
            binding.textviewAno.text = viewModel.getYear().toString()
        }
        
        viewModel.isEmpty.observe(viewLifecycleOwner) { empty ->
            binding.textviewSemEntradas.visibility = if (empty) View.VISIBLE else View.GONE
        }
        
    }
    
    private fun subscribeFragmentResultListeners() {
        setFragmentResultListener(REQUEST_KEY_ENTRADA_DETAILS_CLOSE) { _, bdl ->
            if (bdl.containsKey(FRAGMENT_RESULT_IS_ENTRADA_EDITING) && !bdl.getBoolean(FRAGMENT_RESULT_IS_ENTRADA_EDITING))
            viewModel.fecharDetalhesEntrada()
        }
    }
    
    private fun setUpAdapter() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = EntradaAdapter()
            .apply {
                setInteractionListener(onItemClick = { entradaId ->
                    viewModel.mostrarDetalhes(entradaId)
                })
            }
    }
    
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}