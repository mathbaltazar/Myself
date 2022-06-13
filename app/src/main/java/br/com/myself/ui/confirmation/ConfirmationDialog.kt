package br.com.myself.ui.confirmation

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.myself.R
import br.com.myself.databinding.DialogConfirmationBinding
import br.com.myself.util.Utils.Companion.setUpDimensions

class ConfirmationDialog : DialogFragment(R.layout.dialog_confirmation) {
    
    private val args: ConfirmationDialogArgs by navArgs()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DialogConfirmationBinding.bind(view).apply {
            textviewMessage.text = args.message
            buttonCancelar.setOnClickListener { findNavController().popBackStack() }
            buttonConfirmar.setOnClickListener {
                val bundle = bundleOf("result" to true)
                setFragmentResult(requestKey = args.requestKey, bundle)
                findNavController().popBackStack()
            }
        }
        dialog?.setUpDimensions(95)
    }
    
}