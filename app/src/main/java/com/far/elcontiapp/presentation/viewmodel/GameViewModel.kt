import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.far.elcontiapp.data.model.Jugador
import com.far.elcontiapp.data.repository.JugadoresRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameViewModel : ViewModel() {
    var userEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
    val db = FirebaseFirestore.getInstance()
    // Leer datos de la colección "users"
    val usersCollection = db.collection("users")
    // Lista de jugadores
//    private val _listaJugadoresPartida = MutableStateFlow<List<Jugador>>(emptyList())
//    val listaJugadoresPartida: StateFlow<List<Jugador>> = _listaJugadoresPartida

    // Estados para manejar secciones bloqueadas
    private val _seccionesBloqueadas = MutableStateFlow(List(8) { false })
    val seccionesBloqueadas: StateFlow<List<Boolean>> = _seccionesBloqueadas

    // Puntos por jugador y sección
    private val _puntosPorSeccion = MutableStateFlow(
        (0..7).associateWith { mutableMapOf<Int, Int>() }
    )
    val puntosPorSeccion: StateFlow<Map<Int, MutableMap<Int, Int>>> = _puntosPorSeccion

//    init {
//        cargarJugadoresDePartida()
//    }
//    private fun cargarJugadoresDePartida() {
//        // Simulación: Obtener los datos de jugadores desde otra pantalla o fuente
//        _listaJugadoresPartida.value = JugadoresRepository.listaJugadoresPartida
//
//    }


    private fun recalcularPuntosTotales(jugadorId: Int) {
        // Suma los puntos de todas las secciones para este jugador
        val totalPuntos = _puntosPorSeccion.value.values.sumOf { it[jugadorId] ?: 0 }

        // Actualiza directamente los datos en el repositorio
        JugadoresRepository.listaJugadoresPartida = JugadoresRepository.listaJugadoresPartida.map { jugador ->
            if (jugador.jugadorId == jugadorId) {
                jugador.copy(jugadorPoints = totalPuntos)
            } else {
                jugador
            }
        }.toMutableList() // Convertir de nuevo a MutableList
    }


//    private fun recalcularPuntosTotales(jugadorId: Int) {
//        // Suma los puntos de todas las secciones para este jugador
//        val totalPuntos = _puntosPorSeccion.value.values.sumOf { it[jugadorId] ?: 0 }
//
//        // Actualiza la lista de jugadores con el nuevo total
//        _listaJugadoresPartida.value = _listaJugadoresPartida.value.map { jugador ->
//            if (jugador.jugadorId == jugadorId) {
//                jugador.copy(jugadorPoints = totalPuntos)
//            } else {
//                jugador
//            }
//        }
//    }

    // Actualizar los puntos de un jugador
    fun actualizarPuntosJugador(seccion: Int, jugadorId: Int, puntosNuevos: Int) {
        val mapaSeccion = _puntosPorSeccion.value[seccion]?.toMutableMap() ?: mutableMapOf()
        mapaSeccion[jugadorId] = puntosNuevos
        _puntosPorSeccion.value = _puntosPorSeccion.value.toMutableMap().apply {
            this[seccion] = mapaSeccion
        }

        // Recalcular los puntos totales del jugador
        recalcularPuntosTotales(jugadorId)
    }

//    fun actualizarPuntosJugador(seccion: Int, jugadorId: Int, puntosNuevos: Int) {
//        val mapaSeccion = _puntosPorSeccion.value[seccion]?.toMutableMap() ?: mutableMapOf()
//        mapaSeccion[jugadorId] = puntosNuevos
//        _puntosPorSeccion.value = _puntosPorSeccion.value.toMutableMap().apply {
//            this[seccion] = mapaSeccion
//        }
//
//        // Recalcular los puntos totales del jugador
//        recalcularPuntosTotales(jugadorId)
//    }


    fun obtenerPuntosJugador(seccion: Int, jugadorId: Int): Int {
        return _puntosPorSeccion.value[seccion]?.get(jugadorId) ?: 0
    }

    fun cambiarEstadoSeccion(index: Int) {
        val nuevaLista = _seccionesBloqueadas.value.toMutableList()
        nuevaLista[index] = !nuevaLista[index]
        _seccionesBloqueadas.value = nuevaLista
    }

    fun actualizarPuntosFirebase(onResult: (Boolean) -> Unit) {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
        val db = FirebaseFirestore.getInstance()
        val usuarioRef = db.collection("users").document(userEmail)

        // Obtener el documento del usuario
        usuarioRef.get().addOnSuccessListener { document ->

            if (document.exists()) {
                // Leer la lista de jugadores del documento en Firebase
                val jugadoresFirebase = document.get("jugadores") as? List<Map<String, Any>>
                if (jugadoresFirebase != null) {
                    // Filtrar y actualizar solo los jugadores que están en la lista local
                    val jugadoresActualizados = jugadoresFirebase.map { jugadorFirebase ->
                        val jugadorIdFirebase = (jugadorFirebase["jugadorId"] as? Long)?.toInt()

                        // Verificar si este jugador está en la lista local
                        val jugadorLocal = JugadoresRepository.listaJugadoresPartida.find {
                            it.jugadorId == jugadorIdFirebase
                        }

                        if (jugadorLocal != null) {
                            // Actualizamos los campos para el jugador que participó en la partida
                            jugadorFirebase.toMutableMap().apply {
                                val puntosActuales = jugadorLocal.jugadorPoints ?: 0
                                this["jugadorPoints"] = 0 // Reinicia jugadorPoints a 0
                                this["jugadorTotalPoints"] =
                                    ((this["jugadorTotalPoints"] as? Long) ?: 0) + puntosActuales
                                this["jugadorTotalGames"] =
                                    ((this["jugadorTotalGames"] as? Long) ?: 0) + 1

                                // También actualizamos la lista local
//                                jugadorLocal.jugadorPoints = 0
//                                jugadorLocal.jugadorTotalPoints = ((this["jugadorTotalPoints"] as? Long) ?: 0).toInt()
//                                jugadorLocal.jugadorTotalGames = ((this["jugadorTotalGames"] as? Long) ?: 0).toInt()
                            }
                        } else {
                            // Devolvemos el jugador sin cambios si no participó en la partida
                            jugadorFirebase
                        }
                    }

                    // Guardar la lista actualizada en Firebase
                    usuarioRef.update("jugadores", jugadoresActualizados).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onResult(true)
                        } else {
                            onResult(false)
                        }
                    }
                } else {
                    onResult(false) // No se encontraron jugadores en Firebase
                }
            } else {
                onResult(false) // El documento del usuario no existe
            }
        }.addOnFailureListener {
            onResult(false) // Error al acceder a Firebase
        }
    }

    // Función para obtener los puntos de la partida para un jugador específico

    fun obtenerPuntosDePartida(jugadorId: Int): Long {
        // Recorremos la lista de jugadores para encontrar el jugador con el ID dado
        var puntosPartida = 0L
        JugadoresRepository.listaJugadoresPartida.forEach { jugador ->
            if (jugador.jugadorId == jugadorId) {
                // Devolvemos los puntos del jugador encontrado
                puntosPartida = jugador.jugadorPoints?.toLong() ?: 0
            }
        }
        // Si no se encuentra el jugador, devolvemos 0 como valor por defecto
        return puntosPartida
    }

}
