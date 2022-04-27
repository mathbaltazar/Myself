package br.com.myself.ui.crises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import br.com.myself.databinding.DialogRegistrarCriseBinding
import br.com.myself.model.entity.Crise
import br.com.myself.util.Utils.Companion.setUpDimensions

class RegistrarCriseDialog(
    private val crise: Crise? = null,
    private val onSave: (DialogFragment, Crise) -> Unit
) : DialogFragment() {
    
    private var _binding: DialogRegistrarCriseBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        _binding = DialogRegistrarCriseBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    
    override fun onStart() {
        super.onStart()
    
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setUpDimensions(widthPercent = 85)
    
        setUpView()
    }
    
    private fun setUpView() {
    
        binding.calendarPickerData.apply {
            setOnClickListener { showCalendar(childFragmentManager, null) }
        }
    
        binding.buttonDialogRegistrarCriseSalvar.setOnClickListener {
            salvarCrise()
        }
        
        setUpHorariosDropdown()
        
        if (crise != null) { // EDIÇÃO
            binding.calendarPickerData.setTime(crise.data)
            binding.editTextObservacoes.setText(crise.observacoes)
            binding.dropdownHorario1.setText(crise.horario1, false)
            binding.dropdownHorario2.setText(crise.horario2, false)
        }
    }
    
    private fun salvarCrise() {
        val data = binding.calendarPickerData.getTime()
        val observacoes = binding.editTextObservacoes.text.toString().trim()
        val horario1 = binding.dropdownHorario1.text.toString()
        val horario2 = binding.dropdownHorario2.text.toString()
    
        onSave(this, Crise(
            id = crise?.id,
            data = data,
            observacoes = observacoes,
            horario1 = horario1,
            horario2 = horario2
        ))
    }
    
    private fun setUpHorariosDropdown() {
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item)
        
        for (i in 0..23) {
            for (j in 0..30 step (30)) {
                adapter.add("${if (i < 10) "0$i" else "$i"}:${if (j == 0) "00" else "30"}")
            }
        }
        
        binding.dropdownHorario1.setAdapter(adapter)
        binding.dropdownHorario2.setAdapter(adapter)
    }
    
}
