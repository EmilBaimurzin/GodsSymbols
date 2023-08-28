package com.gods.game.ui.page1

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.gods.game.R
import com.gods.game.databinding.FragmentPage1Binding
import com.gods.game.ui.other.ViewBindingFragment

class FragmentPage1 : ViewBindingFragment<FragmentPage1Binding>(FragmentPage1Binding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            start.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentMain_to_fragmentChoose)
            }

            exit.setOnClickListener {
                requireActivity().finish()
            }

        }

        binding.privacyText.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    ACTION_VIEW,
                    Uri.parse("https://www.google.com")
                )
            )
        }
    }
}