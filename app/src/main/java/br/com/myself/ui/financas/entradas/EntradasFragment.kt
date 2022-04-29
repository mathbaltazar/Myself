package br.com.myself.ui.financas.entradas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.R
import br.com.myself.databinding.FragmentEntradasBinding
import br.com.myself.model.entity.Entrada
import br.com.myself.ui.adapter.EntradaAdapter
import br.com.myself.util.Utils
import br.com.myself.viewmodel.EntradasFragmentViewModel

class EntradasFragment : Fragment(R.layout.fragment_entradas) {
    
    private val viewModel: EntradasFragmentViewModel by viewModels()
    private var _binding: FragmentEntradasBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntradasBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setUpAdapter()
        
        viewModel.entradas.observe(viewLifecycleOwner, {
            (binding.recyclerView.adapter as EntradaAdapter).submitData(lifecycle, it)
    
            atualizarUI()
        })
        
        viewModel.quantidadeEntradas.observe(viewLifecycleOwner, { countEntradas ->
            binding.textviewSemEntradas.visibility =
                if (countEntradas == 0) View.VISIBLE else View.GONE
        })
        
        binding.buttonVoltarAno.setOnClickListener {
            viewModel.voltarAno()
        }
        
        binding.buttonAvancarAno.setOnClickListener {
            viewModel.avancarAno()
        }
        
        binding.buttonAdicionar.setOnClickListener {
            iniciarDialogCriarEntrada()
        }
    }
    
    private fun setUpAdapter() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = EntradaAdapter()
        
        adapter.setClickListener(onItemLongClick = { viewAnchor, entrada ->
            val popupMenu = PopupMenu(requireContext(), viewAnchor)
    
            popupMenu.menu.add(Menu.NONE, 0, Menu.NONE, "Editar").setOnMenuItemClickListener {
                iniciarDialogCriarEntrada(entrada)
                true
            }
    
            popupMenu.menu.add(Menu.NONE, 1, Menu.NONE, "Excluir").setOnMenuItemClickListener {
                confirmarExcluirEntrada(entrada)
                true
            }
            popupMenu.show()
            
        }, onSeparatorClick = {
            // TODO
        })
    
        binding.recyclerView.adapter = adapter
    }
    
    private fun confirmarExcluirEntrada(entrada: Entrada) {
        var mensagem = "Deseja realmente excluir a entrada?"
        mensagem += "\n\nFonte: ${entrada.descricao}"
        mensagem += "\nValor: ${Utils.formatCurrency(entrada.valor)}"
    
        AlertDialog.Builder(requireContext()).setTitle("Excluir").setMessage(mensagem)
            .setPositiveButton("Excluir") { _, _ ->
                viewModel.excluir(entrada, onComplete = {
                    Toast.makeText(context, "Removido!", Toast.LENGTH_SHORT).show()
                })
            }.setNegativeButton("Cancelar", null).show()
    }
    
    private fun atualizarUI() {
        binding.textviewAno.text = "${viewModel.anoAtual}"
    }
    
    private fun iniciarDialogCriarEntrada(entrada: Entrada? = null) {
        val dialog = CriarEntradaDialog(entrada, onSave = { dialog, novaentrada ->
            viewModel.salvar(novaentrada, onComplete = {
                Toast.makeText(context, "Dados salvos!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            })
        })
        dialog.show(childFragmentManager, null)
    }
    
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}