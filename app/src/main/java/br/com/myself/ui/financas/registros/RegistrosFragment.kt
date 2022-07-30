package br.com.myself.ui.financas.registros

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.R
import br.com.myself.data.model.Registro
import br.com.myself.databinding.FragmentRegistrosBinding
import br.com.myself.injectors.provideRegistroRepo
import br.com.myself.ui.adapter.RegistroAdapter
import br.com.myself.util.AdapterClickListener
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
    
        // LABEL MÊS/ANO ACIONA DROPDOWNS
        binding.textViewMesAno.setOnClickListener {
            // TODO Criar componente independente
            /*binding.textViewMesAno.visibility = View.GONE
            binding.componentDropdownMesAno.visibility = View.VISIBLE
            viewModel.showDropdowns()*/
        }
    
        // DROPDOWN MÊS
        binding.dropdownMes.apply {
            // todo
        }
    
        // DROPDOWN ANO
        binding.dropdownAno.apply {
            // todo
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
    
        // INSTANCIAR ADAPTER E CLICKLISTENERS
        setupRegistroAdapter()
        registerObservers()
    }
    
    private fun registerObservers() {
        viewModel.eventsStreamLiveData.observe(viewLifecycleOwner) { event ->
            when (event) {
                is RegistrosViewModel.Event.NavigateToCardDetails -> {
                    val direction = RegistrosFragmentDirections.toCardDetalhesRegistroDest(event.id)
                    findNavController().navigate(direction)
                }
            }
        }
        
        viewModel.registros.observe(viewLifecycleOwner) { registros ->
            (binding.recyclerviewRegistros.adapter as RegistroAdapter).submitList(registros)
            
            val quantidade = registros?.size ?: 0
    
            binding.textViewMesAno.text = viewModel.labelPageFormatado
            binding.textViewTotalMes.text = viewModel.totalMesAtualFormatado
            binding.textViewQuantidadeRegistros.text = "$quantidade"
            binding.textviewNenhumRegistroEncontrado.visibility =
                if (quantidade == 0) View.VISIBLE else View.GONE
        }
        
        viewModel.monthPageLayoutState.observe(viewLifecycleOwner) { layoutState ->
            when(layoutState) {
                is RegistrosViewModel.MonthPageFilterState.LabelState -> {
                    binding.textViewMesAno.visibility = View.VISIBLE
                    binding.componentDropdownMesAno.visibility = View.GONE
                }
                is RegistrosViewModel.MonthPageFilterState.DropdownState -> {
                    binding.textViewMesAno.visibility = View.GONE
                    binding.componentDropdownMesAno.visibility = View.VISIBLE
                }
            }
        }
        
        viewModel.backendNetworkIntegration.observeExpsense(requireContext(), viewLifecycleOwner)
        viewModel.backendNetworkIntegration.state(viewLifecycleOwner) {
        
        }
    
    
    }
    
    private fun setupRegistroAdapter() {
        binding.recyclerviewRegistros.layoutManager = LinearLayoutManager(requireContext())
        val adapter = RegistroAdapter()
        val listener = AdapterClickListener<Registro>(
            onClick = { viewModel.mostrarDetalhes(registroId = it.id!!) }
        )
        adapter.setClickListener(listener)
        binding.recyclerviewRegistros.adapter = adapter
    }
    
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    
}
