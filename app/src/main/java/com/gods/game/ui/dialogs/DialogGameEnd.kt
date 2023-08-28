package com.gods.game.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gods.game.R
import com.gods.game.core.library.ViewBindingDialog
import com.gods.game.databinding.DialogGameEndBinding
import com.gods.game.domain.SP

class DialogGameEnd: ViewBindingDialog<DialogGameEndBinding>(DialogGameEndBinding::inflate) {
    private val args: DialogGameEndArgs by navArgs()
    private val sp by lazy {
        SP(requireContext())
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(false)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                findNavController().popBackStack(R.id.fragmentMain, false, false)
                true
            } else {
                false
            }
        }
        binding.scores.text = args.scores.toString()
        binding.bestScores.text = sp.getBest().toString()

        binding.close.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
        }
        binding.apply.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
        }
    }
}