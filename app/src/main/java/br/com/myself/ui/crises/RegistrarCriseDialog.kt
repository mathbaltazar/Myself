package br.com.myself.ui.crises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import br.com.myself.R
import br.com.myself.domain.entity.Crise
import br.com.myself.domain.repository.CriseRepository
import br.com.myself.observer.Events
import br.com.myself.observer.Trigger
import br.com.myself.util.Async
import br.com.myself.util.Utils.Companion.setUpDimensions
import kotlinx.android.synthetic.main.dialog_registrar_crise.*

class RegistrarCriseDialog(
    private val crise: Crise? = null,
    private val repository: CriseRepository
) : DialogFragment() {
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.dialog_registrar_crise, container, false)
    }
    
    override fun onStart() {
        super.onStart()
    
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setUpDimensions(widthPercent = 85)
    
        setUpView()
    }
    
    private fun setUpView() {
        calendar_picker_dialog_registrar_crise_data.setOnClickListener {
            calendar_picker_dialog_registrar_crise_data.showCalendar(childFragmentManager, null)
        }
        
        button_dialog_registrar_crise_salvar.setOnClickListener {
            salvarCrise()
        }
        
        gerarHorariosDropdown()
        
        if (crise != null) { // EDIÇÃO
            calendar_picker_dialog_registrar_crise_data.setTime(crise.data)
            et_dialog_registrar_crise_observacoes.setText(crise.observacoes)
            dropdown_dialog_registrar_crise_horario1.setText(crise.horario1, false)
            dropdown_dialog_registrar_crise_horario2.setText(crise.horario2, false)
        }
    }
    
    private fun salvarCrise() {
        val data = calendar_picker_dialog_registrar_crise_data.getTime()
        val observacoes = et_dialog_registrar_crise_observacoes.text.toString().trim()
        val horario1 = dropdown_dialog_registrar_crise_horario1.text.toString()
        val horario2 = dropdown_dialog_registrar_crise_horario2.text.toString()
    
        val novaCrise = Crise(
            id = crise?.id,
            data = data,
            observacoes = observacoes,
            horario1 = horario1,
            horario2 = horario2
        )
    
        Async.doInBackground({ repository.salvar(novaCrise) }, {
            Trigger.launch(Events.Toast("Salvo!"), Events.UpdateCrises)
            dismiss()
        })
    }
    
    private fun gerarHorariosDropdown() {
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        
        for (i in 0..23) {
            for (j in 0..30 step (30)) {
                adapter.add("${if (i < 10) "0$i" else "$i"}:${if (j == 0) "00" else "30"}")
            }
        }
        
        dropdown_dialog_registrar_crise_horario1.setAdapter(adapter)
        dropdown_dialog_registrar_crise_horario2.setAdapter(adapter)
    }
    
}
