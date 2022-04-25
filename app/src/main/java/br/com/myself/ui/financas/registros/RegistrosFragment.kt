package br.com.myself.ui.financas.registros

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.R
import br.com.myself.domain.entity.Registro
import br.com.myself.ui.adapter.RegistroAdapter
import br.com.myself.util.AdapterClickListener
import br.com.myself.util.Utils
import br.com.myself.viewmodel.RegistrosFragmentViewModel
import kotlinx.android.synthetic.main.fragment_registros.*
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.toast

class RegistrosFragment : Fragment(R.layout.fragment_registros) {
    
    private lateinit var viewModel : RegistrosFragmentViewModel
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegistrosFragmentViewModel::class.java)
        
        // INSTANCIAR ADAPTER E CLICKLISTENERS
        setupRegistroRecyclerView()
        
        viewModel.registros.observe(viewLifecycleOwner) {
            (recyclerview_registros.adapter as RegistroAdapter).submitList(it)
            atualizarUI()
        }
        
        // MONTH PAGE
        button_page_mes_anterior.setOnClickListener {
            viewModel.mesAnterior()
            resetarLayoutIrPara()
        }
        button_page_proximo_mes.setOnClickListener {
            viewModel.proximoMes()
            resetarLayoutIrPara()
        }
    
        // LABEL MÊS/ANO ACIONA DROPDOWNS
        textview_mes_ano.setOnClickListener {
            textview_mes_ano.visibility = View.GONE
            component_dropdown_mes_ano.visibility = View.VISIBLE
        }
    
        // DROPDOWN MÊS
        dropdown_pesquisa_mes.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, Utils.MESES_STRING))
        dropdown_pesquisa_mes.setOnItemClickListener { _, _, position, _ ->
            viewModel.irParaData(month = position)
        }
    
        // DROPDOWN ANO
        dropdown_pesquisa_ano.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, Utils.ANOS))
        dropdown_pesquisa_ano.setOnItemClickListener { _, _, _, _ ->
            val year = dropdown_pesquisa_ano.text.toString().toInt()
            viewModel.irParaData(year = year)
        }
    
        // AÇÃO BOTÃO PESQUISAR
        button_registros_pesquisar.setOnClickListener {
            requireActivity().startActivity(intentFor<PesquisarRegistrosActivity>())
        }
        
        // AÇÃO BOTÃO ADICIONAR (+)
        button_registros_add.setOnClickListener {
            abrirBottomSheetCriarRegistro(null)
        }
    }
    
    private fun setupRegistroRecyclerView() {
        recyclerview_registros.layoutManager = LinearLayoutManager(requireContext())
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
        recyclerview_registros.adapter = adapter
    }
    
    private fun confirmarExcluirRegistro(registro: Registro, dialog: DetalhesRegistroDialog? = null) {
        var mensagem = "Descrição: ${registro.descricao}"
        mensagem += "\nValor: ${Utils.formatCurrency(registro.valor)}"
    
        AlertDialog.Builder(requireContext()).setTitle("Excluir registro?").setMessage(mensagem)
            .setPositiveButton("Excluir") { _, _ ->
                viewModel.excluirRegistro(registro) {
                    toast("Removido!")
                    dialog?.dismiss()
                }
            }.setNegativeButton("Cancelar", null).show()
    }
    
    private fun atualizarUI() {
        val quantidade = viewModel.registros.value?.size ?: 0
        
        textview_mes_ano.text = viewModel.labelPageFormatado
        tv_registros_total_registros_mes.text = viewModel.totalMesAtualFormatado
        tv_registros_quantidade_registros_mes.text = "$quantidade"
        dropdown_pesquisa_mes.setText(Utils.MESES_STRING[viewModel.month], false)
        dropdown_pesquisa_ano.setText("${viewModel.year}", false)
        
        textview_nenhum_registro_encontrado.visibility =
            if (quantidade == 0) View.VISIBLE else View.GONE
        
    }
    
    private fun resetarLayoutIrPara() {
        textview_mes_ano.visibility = View.VISIBLE
        component_dropdown_mes_ano.visibility = View.GONE
    }
    
    private fun abrirBottomSheetCriarRegistro(registro: Registro?, detalhesDialog: DetalhesRegistroDialog? = null) {
        val bottomSheet = CriarRegistroBottomSheet(registro) { dialog, novoregistro ->
            viewModel.salvarRegistro(novoregistro) {
                toast("Dados salvos!")
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
    
}
