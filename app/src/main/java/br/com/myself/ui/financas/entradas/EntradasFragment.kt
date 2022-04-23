package br.com.myself.ui.financas.entradas

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.R
import br.com.myself.domain.entity.Entrada
import br.com.myself.ui.adapter.EntradaAdapter
import br.com.myself.util.Utils
import br.com.myself.viewmodel.EntradasFragmentViewModel
import kotlinx.android.synthetic.main.fragment_entradas.view.*
import org.jetbrains.anko.support.v4.toast

class EntradasFragment : Fragment(R.layout.fragment_entradas) {
    
    private lateinit var viewModel: EntradasFragmentViewModel
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(EntradasFragmentViewModel::class.java)
    
        setUpAdapter()
        
        viewModel.entradas.observe(viewLifecycleOwner, {
            (view.rv_entradas.adapter as EntradaAdapter).submitData(lifecycle, it)
    
            atualizarUI()
        })
        
        viewModel.quantidadeEntradas.observe(viewLifecycleOwner, { countEntradas ->
            view.tv_entradas_empty.visibility =
                if (countEntradas == 0) View.VISIBLE else View.GONE
        })
        
        view.button_decrement_year.setOnClickListener {
            viewModel.decrementarAno()
        }
        
        view.button_increment_year.setOnClickListener {
            viewModel.incrementarAno()
        }
        
        view.button_entradas_add.setOnClickListener {
            iniciarDialogCriarEntrada()
        }
    }
    
    private fun setUpAdapter() {
        view?.rv_entradas?.layoutManager = LinearLayoutManager(requireContext())
        val adapter = EntradaAdapter()
        
        adapter.setClickListener(onItemLongClick = { viewAnchor, entrada ->
            val popupMenu = PopupMenu(requireContext(), viewAnchor)
    
            popupMenu.menu.add(Menu.NONE, 0, Menu.NONE, "Editar").setOnMenuItemClickListener {
                iniciarDialogCriarEntrada(entrada)
                true
            }
    
            popupMenu.menu.add(Menu.NONE, 1, Menu.NONE, "Excluir").setOnMenuItemClickListener {
                var mensagem = "Deseja realmente excluir a entrada?"
                mensagem += "\n\nFonte: ${entrada.descricao}"
                mensagem += "\nValor: ${Utils.formatCurrency(entrada.valor)}"
        
                AlertDialog.Builder(requireContext()).setTitle("Excluir").setMessage(mensagem)
                    .setPositiveButton("Excluir") { _, _ ->
                        viewModel.excluir(entrada) {
                            toast("Removido!")
                        }
                    }.setNegativeButton("Cancelar", null).show()
                true
            }
            popupMenu.show()
            
        }, onSeparatorClick = {
            // TODO
        })
    
        view?.rv_entradas?.adapter = adapter
    }
    
    private fun atualizarUI() {
        view?.textview_ano?.text = "${viewModel.anoAtual}"
    }
    
    private fun iniciarDialogCriarEntrada(entrada: Entrada? = null) {
        val dialog = CriarEntradaDialog(entrada) { dialog, novaentrada ->
            viewModel.salvar(novaentrada) {
                toast("Dados salvos!")
                dialog.dismiss()
            }
        }
        dialog.show(childFragmentManager, null)
    }
}