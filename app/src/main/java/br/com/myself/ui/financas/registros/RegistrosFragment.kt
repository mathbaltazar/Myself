package br.com.myself.ui.financas.registros

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.R
import br.com.myself.databinding.FragmentRegistrosBinding
import br.com.myself.model.entity.Registro
import br.com.myself.ui.adapter.RegistroAdapter
import br.com.myself.util.AdapterClickListener
import br.com.myself.util.Utils
import br.com.myself.viewmodel.RegistrosFragmentViewModel

class RegistrosFragment : Fragment(R.layout.fragment_registros) {
    
    private val viewModel: RegistrosFragmentViewModel by activityViewModels()
    private var _binding: FragmentRegistrosBinding? = null
    private val binding  get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrosBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // INSTANCIAR ADAPTER E CLICKLISTENERS
        setupRegistroRecyclerView()
        
        viewModel.registros.observe(viewLifecycleOwner) {
            (binding.recyclerviewRegistros.adapter as RegistroAdapter).submitList(it)
            atualizarUI()
        }
        
        // MONTH PAGE
        binding.buttonPageMesAnterior.setOnClickListener {
            viewModel.mesAnterior()
            resetarLayoutIrPara()
        }
        binding.buttonPageProximoMes.setOnClickListener {
            viewModel.proximoMes()
            resetarLayoutIrPara()
        }
    
        // LABEL MÊS/ANO ACIONA DROPDOWNS
        binding.textViewMesAno.setOnClickListener {
            binding.textViewMesAno.visibility = View.GONE
            binding.componentDropdownMesAno.visibility = View.VISIBLE
        }
    
        // DROPDOWN MÊS
        binding.dropdownMes.apply {
            setAdapter(ArrayAdapter(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                Utils.MESES_STRING)
            )
            setOnItemClickListener { _, _, position, _ ->
                viewModel.irParaData(month = position)
            }
        }
    
        // DROPDOWN ANO
        binding.dropdownAno.apply {
            setAdapter(ArrayAdapter(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                Utils.ANOS)
            )
            setOnItemClickListener { _, _, _, _ ->
                val year = text.toString().toInt()
                viewModel.irParaData(year = year)
            }
        }
        
        // AÇÃO BOTÃO PESQUISAR
        binding.buttonPesquisar.setOnClickListener {
            val intent = Intent(context, PesquisarRegistrosActivity::class.java)
            requireActivity().startActivity(intent)
        }
        
        // AÇÃO BOTÃO ADICIONAR (+)
        binding.buttonAdicionar.setOnClickListener {
            abrirBottomSheetCriarRegistro(null)
        }
    }
    
    private fun setupRegistroRecyclerView() {
        binding.recyclerviewRegistros.layoutManager = LinearLayoutManager(requireContext())
        val adapter = RegistroAdapter()
        val listener = AdapterClickListener<Registro>(
            onClick = {
                val dialog = DetalhesRegistroDialog(requireContext(), it)
                dialog.setOnActionListener { action, registro ->
                    when (action) {
                        DetalhesRegistroDialog.ACTION_EDITAR -> abrirBottomSheetCriarRegistro(registro, dialog)
                        DetalhesRegistroDialog.ACTION_EXCLUIR -> confirmarExcluirRegistro(registro, dialog)
                    }
                }
                dialog.show()
            },
            onLongClick = {
                confirmarExcluirRegistro(it)
            }
        )
        adapter.setClickListener(listener)
        binding.recyclerviewRegistros.adapter = adapter
    }
    
    private fun confirmarExcluirRegistro(registro: Registro, dialog: DetalhesRegistroDialog? = null) {
        var mensagem = "Descrição: ${registro.descricao}"
        mensagem += "\nValor: ${Utils.formatCurrency(registro.valor)}"
    
        AlertDialog.Builder(requireContext()).setTitle("Excluir registro?").setMessage(mensagem)
            .setPositiveButton("Excluir") { _, _ ->
                viewModel.excluirRegistro(registro) {
                    Toast.makeText(context, "Removido!", Toast.LENGTH_SHORT).show()
                    dialog?.dismiss()
                }
            }.setNegativeButton("Cancelar", null).show()
    }
    
    private fun atualizarUI() {
        val quantidade = viewModel.registros.value?.size ?: 0
        
        binding.textViewMesAno.text = viewModel.labelPageFormatado
        binding.textViewTotalMes.text = viewModel.totalMesAtualFormatado
        binding.textViewQuantidadeRegistros.text = "$quantidade"
        binding.dropdownMes.setText(Utils.MESES_STRING[viewModel.month], false)
        binding.dropdownAno.setText("${viewModel.year}", false)
        
        binding.textviewNenhumRegistroEncontrado.visibility =
            if (quantidade == 0) View.VISIBLE else View.GONE
        
    }
    
    private fun resetarLayoutIrPara() {
        binding.textViewMesAno.visibility = View.VISIBLE
        binding.componentDropdownMesAno.visibility = View.GONE
    }
    
    private fun abrirBottomSheetCriarRegistro(registro: Registro?, detalhesDialog: DetalhesRegistroDialog? = null) {
        val bottomSheet = CriarRegistroBottomSheet(registro) { dialog, novoregistro ->
            viewModel.salvarRegistro(novoregistro) {
                Toast.makeText(context, "Dados salvos!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                detalhesDialog?.bindData(novoregistro)
            }
        }
        bottomSheet.show(childFragmentManager, null)
    }
    
    override fun onResume() {
        super.onResume()
        resetarLayoutIrPara()
    }
    
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    
}
