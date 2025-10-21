package com.eliascoelho911.cookzy.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<State, Effect>(
    initialState: State
) : ViewModel() {

    private val mutableState = MutableStateFlow(initialState)
    val state: StateFlow<State> = mutableState.asStateFlow()

    private val effectChannel = Channel<Effect>(Channel.BUFFERED)
    val effects: Flow<Effect> = effectChannel.receiveAsFlow()

    protected val currentState: State
        get() = mutableState.value

    protected fun updateState(reducer: (State) -> State) {
        mutableState.update(reducer)
    }

    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch {
            effectChannel.send(effect)
        }
    }
}
