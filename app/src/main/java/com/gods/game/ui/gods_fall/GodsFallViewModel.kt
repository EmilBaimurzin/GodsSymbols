package com.gods.game.ui.gods_fall

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gods.game.core.library.XY
import com.gods.game.core.library.XYIMpl
import com.gods.game.core.library.random
import com.gods.game.domain.Symbol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GodsFallViewModel : ViewModel() {
    var gameState = true
    var pauseState = false
    private var gameScope = CoroutineScope(Dispatchers.Default)
    private val _playerXY = MutableLiveData<XY>()
    val playerXY: LiveData<XY> = _playerXY

    private val _symbols = MutableLiveData<List<Symbol>>(emptyList())
    val symbols: LiveData<List<Symbol>> = _symbols

    private val _lives = MutableLiveData(3)
    val lives: LiveData<Int> = _lives

    private val _symbolToCatch = MutableLiveData(1)
    val symbolToCatch: LiveData<Int> = _symbolToCatch

    private val _scores = MutableLiveData(0)
    val scores: LiveData<Int> = _scores

    fun start(
        symbolSize: Int,
        maxX: Int,
        fallDelay: Long,
        maxY: Int,
        playerWidth: Int,
        playerHeight: Int,
        distance: Int
    ) {
        gameScope = CoroutineScope(Dispatchers.Default)
        generateSymbols(symbolSize, maxX)

        letItemsFall(fallDelay, maxY, symbolSize, playerWidth, playerHeight, distance)

        shuffleSymbols()
    }

    fun stop() {
        gameScope.cancel()
    }

    private fun shuffleSymbols() {
        viewModelScope.launch {
            _symbolToCatch.postValue(1 random 7)
        }
    }

    private fun generateSymbols(symbolSize: Int, maxX: Int) {
        gameScope.launch {
            while (true) {
                delay(2000)
                val currentList = _symbols.value!!.toMutableList()
                currentList.add(
                    Symbol(
                        y = 0f - symbolSize,
                        x = (0 random (maxX - symbolSize)).toFloat(),
                        value = 1 random 7
                    )
                )
                _symbols.postValue(currentList)
            }
        }
    }

    private fun letItemsFall(
        fallDelay: Long,
        maxY: Int,
        symbolSize: Int,
        playerWidth: Int,
        playerHeight: Int,
        distance: Int
    ) {
        gameScope.launch {
            while (true) {
                delay(fallDelay)
                _symbols.postValue(
                    moveSomethingDown(
                        maxY,
                        symbolSize,
                        symbolSize,
                        playerWidth,
                        playerHeight,
                        _symbols.value!!.toMutableList(),
                        { value ->
                            if ((value as Symbol).value == _symbolToCatch.value!!) {
                                _scores.postValue(_scores.value!! + 1)
                                shuffleSymbols()
                            } else {
                                _lives.postValue(_lives.value!! - 1)
                            }
                        }, { value ->
                            if ((value as Symbol).value == _symbolToCatch.value!!) {
                                _lives.postValue(_lives.value!! - 1)
                            }
                        },
                        distance = distance
                    ).toList() as List<Symbol>
                )
            }
        }
    }

    private suspend fun moveSomethingDown(
        maxY: Int,
        objHeight: Int,
        objWidth: Int,
        playerWidth: Int,
        playerHeight: Int,
        oldList: MutableList<XY>,
        onIntersect: ((XY) -> Unit) = {},
        onOutOfScreen: ((XY) -> Unit) = {},
        distance: Int
    ): MutableList<XY> {
        return suspendCoroutine { cn ->
            val newList = mutableListOf<XY>()
            oldList.forEach { obj ->
                if (obj.y <= maxY) {
                    val objX = obj.x.toInt()..obj.x.toInt() + objWidth
                    val objY = obj.y.toInt()..obj.y.toInt() + objHeight
                    val playerX =
                        _playerXY.value!!.x.toInt().._playerXY.value!!.x.toInt() + playerWidth
                    val playerY =
                        _playerXY.value!!.y.toInt().._playerXY.value!!.y.toInt() + playerHeight
                    if (playerX.any { it in objX } && playerY.any { it in objY }) {
                        onIntersect.invoke(obj)
                    } else {
                        obj.y = obj.y + distance
                        obj.x = obj.x
                        newList.add(obj)
                    }
                } else {
                    onOutOfScreen.invoke(obj)
                }
            }
            cn.resume(newList)
        }
    }

    fun initPlayer(x: Int, y: Int, playerWidth: Int) {
        _playerXY.postValue(
            XYIMpl(
                x = x / 2 - (playerWidth.toFloat() / 2),
                y = y - playerWidth.toFloat() - (playerWidth.toFloat() / 5)
            )
        )
    }

    fun playerMoveLeft(limit: Float) {
        if (_playerXY.value!!.x - 4f > limit) {
            _playerXY.postValue(XYIMpl(_playerXY.value!!.x - 4, _playerXY.value!!.y))
        }
    }

    fun playerMoveRight(limit: Float) {
        if (_playerXY.value!!.x + 4f < limit) {
            _playerXY.postValue(XYIMpl(_playerXY.value!!.x + 4, _playerXY.value!!.y))
        }
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }
}