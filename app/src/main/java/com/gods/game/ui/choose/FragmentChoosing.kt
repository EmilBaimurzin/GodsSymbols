package com.gods.game.ui.choose

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.gods.game.databinding.FragmentChoosingBinding
import com.gods.game.ui.other.ViewBindingFragment

class FragmentChoosing: ViewBindingFragment<FragmentChoosingBinding>(FragmentChoosingBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            back.setOnClickListener {
                findNavController().popBackStack()
            }
            menu.setOnClickListener {
                findNavController().popBackStack()
            }
            cleopatra.setOnClickListener {
                findNavController().navigate(FragmentChoosingDirections.actionFragmentChooseToFragmentGodsFall(false))
            }
            zeus.setOnClickListener {
                findNavController().navigate(FragmentChoosingDirections.actionFragmentChooseToFragmentGodsFall(true))
            }
        }
    }
}