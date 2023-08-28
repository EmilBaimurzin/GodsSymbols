package com.gods.game.domain

import com.gods.game.core.library.XY

data class Symbol(
    override var x: Float,
    override var y: Float,
    val value: Int
) : XY