package br.com.myself.ui.financas.despesas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.R
import br.com.myself.domain.entity.Despesa
import br.com.myself.ui.adapter.DespesaAdapter
import br.com.myself.util.Utils
import br.com.myself.viewmodel.DespesasFragmentViewModel
import kotlinx.android.synthetic.main.fragment_despesas.*
import org.jetbrains.anko.support.v4.toast

class DespesasFragment : Fragment(R.layout.fragment_despesas) {
    
    private lateinit var viewModel: DespesasFragmentViewModel
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(DespesasFragmentViewModel::class.java)
        
        
        button_despesas_info.setOnClickListener {
            card_despesas_info.visibility = View.VISIBLE
            card_despesas_info.animation = null
        }
        
        button_card_despesas_info_close.setOnClickListener {
            card_despesas_info.animation =
                AnimationUtils.loadAnimation(it.context, android.R.anim.fade_out)
            card_despesas_info.visibility = View.GONE
        }
        
        fab_despesas_adicionar.setOnClickListener {
            CriarDespesaDialog(it.context) { dialog, despesa ->
                viewModel.salvar(despesa) {
                    toast("Salvo!")
                    dialog.dismiss()
                }
            }.show()
        }
    
        setUpAdapter()
    
        viewModel.despesas.observe(viewLifecycleOwner) { despesas ->
            (recyclerview_despesas.adapter as DespesaAdapter).submitList(despesas)
            tv_despesas_sem_depesas.visibility =
                if (despesas.isEmpty()) View.VISIBLE else View.GONE
        }
        
    }
    
    private fun setUpAdapter() {
        val adapter = DespesaAdapter()
        recyclerview_despesas.adapter = adapter
        recyclerview_despesas.layoutManager = LinearLayoutManager(requireContext())
        adapter.setOnItemActionListener { action, despesa ->
            when (action) {
                DespesaAdapter.ACTION_EXCLUIR -> confirmarExcluirDespesa(despesa)
                DespesaAdapter.ACTION_DETALHES -> {
                    startActivity(Intent(context,
                        DetalhesDespesaActivity::class.java).apply {
                            putExtra(DetalhesDespesaActivity.DESPESA_ID,
                                despesa.id)
                        })
                }
                DespesaAdapter.ACTION_REGISTRAR -> {
                    viewModel.getSugestoes(despesa) { sugestoes ->
                        val dialog =
                            RegistrarDespesaDialog(despesa, sugestoes) { dialog, valor, data ->
                                viewModel.registrarDespesa(despesa, valor, data) {
                                    toast("Registrado!")
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
                    toast("Removido!")
                }
            }.setNegativeButton("Cancelar", null)
            .show()
    }
    
}
