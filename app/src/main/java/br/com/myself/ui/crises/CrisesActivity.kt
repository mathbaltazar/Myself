package br.com.myself.ui.crises

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.R
import br.com.myself.databinding.ActivityCrisesBinding
import br.com.myself.model.entity.Crise
import br.com.myself.ui.adapter.CrisesAdapter
import br.com.myself.util.Utils.Companion.formattedDate
import br.com.myself.viewmodel.CrisesActivityViewModel
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import org.jetbrains.anko.toast

class CrisesActivity : AppCompatActivity() {
    
    private val binding: ActivityCrisesBinding by lazy { ActivityCrisesBinding.inflate(layoutInflater) }
    private val viewModel: CrisesActivityViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpView()
        
        viewModel.crises.observe(this, { crises ->
            (binding.recyclerViewCrises.adapter as CrisesAdapter).submitList(crises)
    
            binding.textViewSemCrisesRegistradas.visibility =
                if (crises.isEmpty()) View.VISIBLE else View.GONE
    
            binding.textViewNumeroCrises.text = crises.size.toString()
        })
    }
    
    @SuppressLint("SetTextI18n")
    private fun setUpView() {
        
        binding.buttonMaisDetalhes.setOnClickListener {
            // TOGGLE LAYOUT MAIS DETALHES
            if (binding.layoutMaisDetalhes.visibility != View.VISIBLE) {
                binding.layoutMaisDetalhes.visibility = View.VISIBLE
                binding.buttonMaisDetalhes.setIconResource(R.drawable.ic_arrow_up)
                binding.buttonMaisDetalhes.text = "Menos"
            } else {
                binding.layoutMaisDetalhes.visibility = View.GONE
                binding.buttonMaisDetalhes.setIconResource(R.drawable.ic_arrow_down)
                binding.buttonMaisDetalhes.text = "Mais"
            }
        }
        
        binding.buttonRegistrarCrise.setOnClickListener {
            abrirDialogRegistrarCrise()
        }
        
        configureAdapter()
    }
    
    private fun configureAdapter() {
        binding.recyclerViewCrises.apply {
            val adapter = CrisesAdapter()
            adapter.setOnItemClickListener { crise, view ->
                showPopupMenu(crise, view)
            }
            this.adapter = adapter
            this.layoutManager = LinearLayoutManager(this@CrisesActivity)
        }
    }
    
    private fun showPopupMenu(crise: Crise, view: View) {
        val popup = PopupMenu(this, view, Gravity.END)
        popup.menu.add("Editar").setOnMenuItemClickListener {
            abrirDialogRegistrarCrise(crise)
            true
        }
        
        popup.menu.add("Excluir").setOnMenuItemClickListener {
            confirmarExcluirCrise(crise)
            true
        }
        popup.show()
    }
    
    private fun abrirDialogRegistrarCrise(crise: Crise? = null) {
        val dialog = RegistrarCriseDialog(crise, onSave = { dialog, novacrise ->
            viewModel.salvarCrise(novacrise) {
                toast("Salvo!")
                dialog.dismiss()
            }
        })
        dialog.show(supportFragmentManager, null)
    }
    
    private fun confirmarExcluirCrise(crise: Crise) {
        var mensagem = "Data: ${crise.data.formattedDate()}"
        mensagem += "\nHorários: Entre ${crise.horario1} e ${crise.horario2}"
        mensagem += "\nObservações: ${crise.observacoes}"
        
        AlertDialog.Builder(this).setTitle("Excluir").setMessage(mensagem)
            .setPositiveButton("Excluir") { _, _ ->
                
                viewModel.excluirCrise(crise) {
                    toast("Removido!")
                }
                
            }.setNegativeButton("Cancelar", null).show()
    }
    
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
    
}
