package br.com.myself.ui.financas.despesas

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.R
import br.com.myself.databinding.FragmentDespesasBinding
import br.com.myself.model.entity.Despesa
import br.com.myself.ui.adapter.DespesaAdapter
import br.com.myself.util.Utils
import br.com.myself.viewmodel.DespesasFragmentViewModel

class DespesasFragment : Fragment(R.layout.fragment_despesas) {
    
    private val viewModel: DespesasFragmentViewModel by viewModels()
    private var _binding: FragmentDespesasBinding? = null
    private val binding get() = _binding!!
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDespesasBinding.bind(view)
        
        binding.buttonCardDespesasInfoClose.setOnClickListener {
            binding.cardDespesasInfo.animation =
                AnimationUtils.loadAnimation(it.context, android.R.anim.slide_out_right)
            binding.cardDespesasInfo.visibility = View.GONE
        }
        
        binding.buttonAdicionar.setOnClickListener {
            CriarDespesaDialog(it.context) { dialog, despesa ->
                viewModel.salvar(despesa) {
                    Toast.makeText(context, "Salvo!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }.show()
        }
    
        setUpAdapter()
        subscribeObservers()
        setupMenu()
    }
    
    private fun setupMenu() {
        val menuHost = requireActivity() as MenuHost
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_fragment_despesas, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.menu_item_info_despesa) {
                    binding.cardDespesasInfo.visibility = View.VISIBLE
                    binding.cardDespesasInfo.animation =
                        AnimationUtils.loadAnimation(requireContext(), android.R.anim.slide_in_left)
                }
                return true
            }
        }, viewLifecycleOwner)
    }
    
    private fun subscribeObservers() {
        viewModel.despesas.observe(viewLifecycleOwner) { despesas ->
            (binding.recyclerviewDespesas.adapter as DespesaAdapter).submitList(despesas)
            binding.textviewSemDepesas.visibility =
                if (despesas.isEmpty()) View.VISIBLE else View.GONE
        }
    }
    
    private fun setUpAdapter() {
        val adapter = DespesaAdapter()
        binding.recyclerviewDespesas.adapter = adapter
        binding.recyclerviewDespesas.layoutManager = LinearLayoutManager(requireContext())
        adapter.setOnItemActionListener { action, despesa ->
            when (action) {
                DespesaAdapter.ACTION_EXCLUIR -> confirmarExcluirDespesa(despesa)
                DespesaAdapter.ACTION_DETALHES -> startActivity(DetalhesDespesaActivity.getIntent(requireContext(), despesa))
                DespesaAdapter.ACTION_REGISTRAR -> {
                    viewModel.getSugestoes(despesa) { sugestoes ->
                        val dialog =
                            RegistrarDespesaDialog(despesa, sugestoes) { dialog, valor, data ->
                                viewModel.registrarDespesa(despesa, valor, data) {
                                    Toast.makeText(context, "Registrado!", Toast.LENGTH_SHORT)
                                        .show()
                                    dialog.dismiss()
                                }
                            }
                        dialog.show(childFragmentManager, null)
                    }
                }
            }
        }
    }
    
    private fun confirmarExcluirDespesa(despesa: Despesa) {
        var mensagem = "Nome: ${despesa.nome}"
        mensagem += "\nValor: ${Utils.formatCurrency(despesa.valor)}"
        if (despesa.diaVencimento != 0) mensagem += "\nVencimento: ${despesa.diaVencimento}"
        
        AlertDialog.Builder(requireContext()).setTitle("Excluir despesa?")
            .setMessage(mensagem)
            .setPositiveButton("Excluir") { _, _ ->
                viewModel.excluir(despesa) {
                    Toast.makeText(context, "Removido!", Toast.LENGTH_SHORT).show()
                }
            }.setNegativeButton("Cancelar", null)
            .show()
    }
    
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
