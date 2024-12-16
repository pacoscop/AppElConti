package com.far.elcontiapp.data.model

import com.google.firebase.firestore.FirebaseFirestore

data class Jugador(
    val jugadorId: Int?,
    val jugadorName: String?,
    var jugadorPoints: Int?,
    var jugadorTotalPoints: Int?,
    var jugadorTotalGames: Int?,

    ){
    fun toMap(): MutableMap<String, *> {
        return mutableMapOf(
            "jugadorId" to this.jugadorId,
            "jugadorName" to this.jugadorName,
            "jugadorPoints" to this.jugadorPoints,
            "jugadorTotalPoints" to this.jugadorTotalPoints,
            "jugadorTotalGames" to this.jugadorTotalGames,

        )
    }

/*
    fun generarID(): Int {
        var list: List<Jugador>() =

        FirebaseFirestore.getInstance().collection("User")
            .document("jugadores")

        if(list) {

        }
    }*/
}