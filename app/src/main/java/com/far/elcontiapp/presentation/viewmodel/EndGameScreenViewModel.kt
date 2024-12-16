package com.far.elcontiapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.far.elcontiapp.data.model.Jugador
import com.far.elcontiapp.data.repository.JugadoresRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EndGameScreenViewModel: ViewModel() {
    fun resetListaJugadores() {
        JugadoresRepository.listaJugadoresPartida.clear()
    }
}