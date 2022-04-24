package br.com.myself.ui.crises

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.myself.R
import br.com.myself.domain.entity.Crise
import br.com.myself.ui.adapter.CrisesAdapter
import br.com.myself.util.Utils.Companion.formattedDate
import br.com.myself.viewmodel.CrisesActivityViewModel
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_crises.*
import org.jetbrains.anko.toast

class CrisesActivity : AppCompatActivity() {
    
    private lateinit var viewModel: CrisesActivityViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crises)
        
        viewModel = ViewModelProvider(this).get(CrisesActivityViewModel::class.java)
        
        setUpView()
    
        viewModel.crises.observe(this, { crises ->
            (recycler_view_crises.adapter as CrisesAdapter).submitList(crises)
    
            tv_crises_sem_crises_registradas.visibility =
                if (crises.isEmpty()) View.VISIBLE else View.GONE
    
            tv_crises_numero_crises.text = crises.size.toString()
        })
    }
    
    @SuppressLint("SetTextI18n")
    private fun setUpView() {
        button_crises_mais_detalhes.setOnClickListener {
            // TOGGLE LAYOUT MAIS DETALHES
            if (ll_crises_mais_detalhes.visibility != View.VISIBLE) {
                ll_crises_mais_detalhes.visibility = View.VISIBLE
                button_crises_mais_detalhes.setIconResource(R.drawable.ic_arrow_up)
                button_crises_mais_detalhes.text = "Menos"
            } else {
                ll_crises_mais_detalhes.visibility = View.GONE
                button_crises_mais_detalhes.setIconResource(R.drawable.ic_arrow_down)
                button_crises_mais_detalhes.text = "Mais"
            }
        }
    
        button_crises_registrar_crise.setOnClickListener {
            abrirDialogRegistrarCrise()
        }
    
        configureAdapter()
    }
    
    private fun configureAdapter() {
        recycler_view_crises.adapter = CrisesAdapter().apply {
            setOnItemClickListener { crise, view ->
                showPopupMenu(crise, view)
            }
        }
        
        recycler_view_crises.layoutManager = LinearLayoutManager(this)
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
        val dialog = RegistrarCriseDialog(crise) { dialog,  novacrise ->
            viewModel.salvarCrise(novacrise) {
                toast("Salvo!")
                dialog.dismiss()
            }
        }
        dialog.show(supportFragmentManager, null)
    }
    
    private fun confirmarExcluirCrise(crise: Crise) {
        var mensagem = "Data: ${crise.data.formattedDate()}"
        mensagem += "\nHorários: Entre ${crise.horario1} e ${crise.horario2}"
        mensagem += "\nObservações: ${crise.observacoes}"
    
        AlertDialog.Builder(this).setTitle("Excluir")
            .setMessage(mensagem)
            .setPositiveButton("Excluir") { _, _ ->
                
                viewModel.excluirCrise(crise) {
                    toast("Removido!")
                }
    
            }.setNegativeButton("Cancelar", null)
            .show()
    }
    
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
    
}
