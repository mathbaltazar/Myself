package br.com.myself.ui.financas.registros

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.R
import br.com.myself.databinding.FragmentPesquisarRegistrosBinding
import br.com.myself.injectors.provideRegistroRepo
import br.com.myself.ui.adapter.RegistroAdapter
import br.com.myself.util.AdapterClickListener
import br.com.myself.viewmodel.PesquisarRegistrosViewModel

class PesquisarRegistrosFragment : Fragment(R.layout.fragment_pesquisar_registros) {
    
    private val viewModel: PesquisarRegistrosViewModel by viewModels {
        PesquisarRegistrosViewModel.Factory(provideRegistroRepo()) }
    
    private var _binding: FragmentPesquisarRegistrosBinding? = null
    private val binding get() = _binding!!
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPesquisarRegistrosBinding.bind(requireView())
        
        configureAdapter()
        
        binding.textInputLayoutBusca.apply {
            setEndIconOnClickListener {
                iniciarBusca(editText?.text.toString().trim())
            }
            editText?.setOnEditorActionListener { _, _, _ ->
                iniciarBusca(editText?.text.toString().trim())
            }
        }
        
        viewModel.resultadoBusca.observe(viewLifecycleOwner) {
            binding.textInputLayoutBusca.helperText = "Resultados: ${viewModel.resultCount}"
            
            (binding.recyclerView.adapter as RegistroAdapter).submitList(it)
            
            binding.textViewNenhumaBusca.visibility =
                if (viewModel.hasAnyResult()) View.VISIBLE else View.GONE
        }
    }
    
    private fun configureAdapter() {
        val adapter = RegistroAdapter()
        adapter.setClickListener(AdapterClickListener(onClick = {
            val direction = PesquisarRegistrosFragmentDirections.toCardDetalhesRegistroDest(it.id!!)
            findNavController().navigate(direction)
        }))
        
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }
    
    private fun iniciarBusca(busca: String): Boolean {
        if (busca.isNotBlank()) {
            viewModel.setBusca(busca)
        } else {
            (binding.recyclerView.adapter as RegistroAdapter).submitList(null)
            binding.textViewNenhumaBusca.visibility = View.VISIBLE
            binding.textInputLayoutBusca.helperText = "Resultados: 0"
        }
        return true
    }
    
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}