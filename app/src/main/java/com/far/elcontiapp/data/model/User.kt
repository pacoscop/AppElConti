package com.far.elcontiapp.data.model

import com.google.firebase.firestore.FirebaseFirestore

data class User(
    val userId: String?,
    val userEmail: String?,
    val jugadores: List<Jugador>?

){
    fun toMap(): MutableMap<String, *> {
        return mutableMapOf(
            "userId" to this.userId,
            "userEmail" to this.userEmail,
            "jugadores" to this.jugadores,
        )
    }

    fun getJugadorId(): Int {
        var count = 0
        if(jugadores.isNullOrEmpty()) {
            count = 1
        }else{
            count = jugadores.size + 1
        }
        return count
    }

    /*
    fun inicializarJugadorId(jugador: ) {
        var count = 0
        if(FirebaseFirestore.getInstance().collection("User")
            .document(jugador["jugadorId"] == null)) {
            jugadores?.get(count)?.jugadorId = 1
        }else{
            var count = jugadores.size
            jugadores.get(count).jugadorId = count+1
        }

    }*/
}