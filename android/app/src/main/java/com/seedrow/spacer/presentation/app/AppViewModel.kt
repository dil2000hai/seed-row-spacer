package com.seedrow.spacer.presentation.app

import androidx.lifecycle.ViewModel
import com.seedrow.spacer.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val backStack = mutableListOf<Screen>()

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current !is Screen.Splash) {
            backStack.add(current)
        }
        _screen.value = target
    }

    fun enterMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
    }

    fun back() {
        if (backStack.isEmpty()) {
            _screen.value = Screen.Menu
            return
        }
        _screen.value = backStack.removeAt(backStack.size - 1)
    }
}
