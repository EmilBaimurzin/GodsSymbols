package com.gods.game.ui.gods_fall

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gods.game.R
import com.gods.game.databinding.FragmentGodsFallBinding
import com.gods.game.domain.SP
import com.gods.game.ui.other.ViewBindingFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class FragmentGodsFall :
    ViewBindingFragment<FragmentGodsFallBinding>(FragmentGodsFallBinding::inflate) {
    private var moveScope = CoroutineScope(Dispatchers.Default)
    private val viewModel: GodsFallViewModel by viewModels()
    private val xy by lazy {
        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        Pair(size.x, size.y)
    }
    private lateinit var playerView: ImageView
    private val sp by lazy {
        SP(requireContext())
    }
    private val args: FragmentGodsFallArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtons()
        binding.playerLayout.removeAllViews()

        if (args.isZeus) {
            playerView = ImageView(requireContext())
            playerView.setImageResource(R.drawable.zeus)
            playerView.apply {
                layoutParams = ViewGroup.LayoutParams(dpToPx(120), dpToPx(150))
                scaleX = 1f
                scaleY = 1f
                setImageResource(R.drawable.zeus)
                binding.playerLayout.addView(playerView)
            }
        } else {
            playerView = ImageView(requireContext())
            playerView.apply {
                layoutParams = ViewGroup.LayoutParams(dpToPx(90), dpToPx(150))
                scaleX = 1.4f
                scaleY = 1.4f
                setImageResource(R.drawable.cleopatra)
            }
            binding.playerLayout.addView(playerView)
        }

        binding.menu.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
        }

        viewModel.playerXY.observe(viewLifecycleOwner) {
            playerView.x = it.x
            playerView.y = it.y
        }

        viewModel.symbols.observe(viewLifecycleOwner) {
            binding.symbolsLayout.removeAllViews()
            it.forEach { symbol ->
                val symbolView = ImageView(requireContext())
                symbolView.layoutParams = ViewGroup.LayoutParams(dpToPx(80), dpToPx(80))
                val symbolImg = when (symbol.value) {
                    1 -> R.drawable.symbol01
                    2 -> R.drawable.symbol02
                    3 -> R.drawable.symbol03
                    4 -> R.drawable.symbol04
                    5 -> R.drawable.symbol05
                    6 -> R.drawable.symbol06
                    else -> R.drawable.symbol07
                }
                symbolView.setImageResource(symbolImg)
                symbolView.x = symbol.x
                symbolView.y = symbol.y
                binding.symbolsLayout.addView(symbolView)
            }
        }

        viewModel.lives.observe(viewLifecycleOwner) {
            binding.livesLayout.removeAllViews()
            repeat(it) {
                val liveView = ImageView(requireContext())
                liveView.layoutParams = LinearLayout.LayoutParams(dpToPx(30), dpToPx(30)).apply {
                    marginEnd = dpToPx(3)
                    marginStart = dpToPx(3)
                }
                liveView.setImageResource(R.drawable.live)
                binding.livesLayout.addView(liveView)
            }

            if (it == 0 && viewModel.gameState) {
                end()
            }
        }

        viewModel.symbolToCatch.observe(viewLifecycleOwner) {
            binding.goal.setImageResource(
                when (it) {
                    1 -> R.drawable.symbol01
                    2 -> R.drawable.symbol02
                    3 -> R.drawable.symbol03
                    4 -> R.drawable.symbol04
                    5 -> R.drawable.symbol05
                    6 -> R.drawable.symbol06
                    else -> R.drawable.symbol07
                }
            )
        }

        viewModel.scores.observe(viewLifecycleOwner) {
            binding.scores.text = it.toString()
        }

        lifecycleScope.launch {
            delay(20)
            if (viewModel.playerXY.value == null) {
                viewModel.initPlayer(xy.first, xy.second, playerView.height)
            }
            if (viewModel.gameState && !viewModel.pauseState) {
                viewModel.start(
                    dpToPx(80),
                    xy.first,
                    16,
                    xy.second,
                    playerView.width,
                    playerView.height,
                    6
                )
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    private fun end() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.stop()
            viewModel.gameState = false
            if (viewModel.scores.value!! > sp.getBest()) {
                sp.setBest(viewModel.scores.value!!)
            }
                findNavController().navigate(
                    FragmentGodsFallDirections.actionFragmentGodsFallToDialogGameEnd(
                        viewModel.scores.value!!
                    )
                )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setButtons() {
        binding.buttonLeft.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    moveScope.launch {
                        while (true) {
                            viewModel.playerMoveLeft(0f)
                            delay(2)
                        }
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    moveScope.launch {
                        while (true) {
                            viewModel.playerMoveLeft(0f)
                            delay(2)
                        }
                    }
                    true
                }

                else -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    false
                }
            }
        }

        binding.buttonRight.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    moveScope.launch {
                        while (true) {
                            viewModel.playerMoveRight((xy.first - playerView.width).toFloat())
                            delay(2)
                        }
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    moveScope.launch {
                        while (true) {
                            viewModel.playerMoveRight((xy.first - playerView.width).toFloat())
                            delay(2)
                        }
                    }
                    true
                }

                else -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    false
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.stop()
    }
}