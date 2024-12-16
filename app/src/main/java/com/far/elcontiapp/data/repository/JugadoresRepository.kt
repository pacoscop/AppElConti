package com.far.elcontiapp.data.repository

import com.far.elcontiapp.data.model.Jugador

object JugadoresRepository { //esto nos sirve para hacer la lista de jugadores de la partida estática
    var listaJugadoresPartida: MutableList<Jugador> = mutableListOf()
}