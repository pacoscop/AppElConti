package com.far.elcontiapp.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.far.elcontiapp.data.model.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import androidx.navigation.NavController
import com.far.elcontiapp.data.model.Jugador
import com.google.firebase.firestore.FieldValue

/*
class AddPlayerScreenViewModel(application: Application) : AndroidViewModel(application) {*/

class AddPlayerScreenViewModel: ViewModel() {

    var userEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
    val db = FirebaseFirestore.getInstance()

    // Leer datos de la colección "users"
    val usersCollection = db.collection("users")

    // Lista de jugadores para la partida (HashMap LazyList)
    var listaJugadoresPartida =
        mutableStateOf(listOf<Jugador>()) // Nombre -> ID del jugador
        private set

    // Lista de jugadores existentes
    var listaJugadoresExistentes = mutableStateOf(listOf<Jugador>())
        private set

    // Jugador seleccionado
    var jugadorSeleccionado = mutableStateOf<Jugador?>(null)

    // Mensaje de confirmación para borrar
    var mostrarConfirmacionBorrar = mutableStateOf(false)

    fun obtenerJugadorConIdCero() {
        val db = FirebaseFirestore.getInstance()
        val userDocumentRef = db.collection("users").document(userEmail)

        userDocumentRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val jugadores = document.get("jugadores") as? List<Map<String, Any>>
                    val jugadorConIdCero = jugadores?.firstOrNull { jugador ->
                        jugador["jugadorId"]?.toString()?.toInt() == 0
                    }
                    if (jugadorConIdCero != null) {
                        val jugador = Jugador(
                            jugadorId = jugadorConIdCero["jugadorId"]?.toString()?.toInt() ?: 0,
                            jugadorName = jugadorConIdCero["jugadorName"]?.toString() ?: "",
                            jugadorPoints = jugadorConIdCero["jugadorPoints"]?.toString()?.toInt() ?: 0,
                            jugadorTotalGames = jugadorConIdCero["jugadorTotalGames"]?.toString()?.toInt() ?: 0,
                            jugadorTotalPoints = jugadorConIdCero["jugadorTotalPoints"]?.toString()?.toInt() ?: 0
                        )

                        //listaJugadoresPartida.value["Jugador1"] = jugador.jugadorName.toString()
                    } else {
                        // No se encontró jugador con id 0
                    }
                } else {
                    // El documento del usuario no existe
                }
            }
            .addOnFailureListener { exception ->
                 // Error al obtener el documento
            }
    }

    // Cargar jugadores desde Firebase
    fun cargarJugadores(userEmail: String) {
        usersCollection.document(userEmail).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val jugadores = document.get("jugadores") as? List<Map<String, Any>>
                val jugadoresList = mutableListOf<Jugador>()

                jugadores?.forEach { jugadorMap ->
                    val jugador = Jugador(
                        jugadorId = (jugadorMap["jugadorId"] as? Long)?.toInt() ?: 0,
                        jugadorName = jugadorMap["jugadorName"] as? String ?: "",
                        jugadorPoints = (jugadorMap["jugadorPoints"] as? Long)?.toInt() ?: 0,
                        jugadorTotalPoints = (jugadorMap["jugadorTotalPoints"] as? Long)?.toInt()
                            ?: 0,
                        jugadorTotalGames = (jugadorMap["jugadorTotalGames"] as? Long)?.toInt() ?: 0
                    )
                    jugadoresList.add(jugador)
                }

                // Actualiza la lista con los jugadores de este usuario
                listaJugadoresExistentes.value = jugadoresList
            } else {
                println("El documento del usuario no existe.")
            }
        }
            .addOnFailureListener { exception ->
                println("Error al cargar los jugadores: ${exception.message}")
            }
    }        /*
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        FirebaseFirestore.getInstance().collection("users").document(userId!!).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                listaJugadoresExistentes.value = user?.jugadores ?: emptyList()
            }
         */


    // Añadir un jugador a la partida

    fun agregarJugadorAPartida(userEmail: String, jugador: Jugador, onJugadorYaEnPartida: () -> Unit) {

        usersCollection.document(userEmail).get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Verificamos si el jugador ya está en la lista
                val jugadorYaExiste = listaJugadoresPartida.value.any { it.jugadorId == jugador.jugadorId }

                if (jugadorYaExiste) {
                    // Llamamos a la función para mostrar el mensaje "Jugador ya en la partida"
                    onJugadorYaEnPartida()
                } else {
                val jugadoresPartida = document.get("jugadoresPartida") as? List<Map<String, Any>> ?: emptyList()
                val jugadorMap: Map<String, Any> = mapOf(
                    "jugadorId" to (jugador.jugadorId ?: 0),
                    "jugadorName" to (jugador.jugadorName ?: ""),
                    "jugadorPoints" to (jugador.jugadorPoints ?: 0),
                    "jugadorTotalPoints" to (jugador.jugadorTotalPoints ?: 0),
                    "jugadorTotalGames" to (jugador.jugadorTotalGames ?: 0)
                )

                // Actualizamos la lista local en lugar de Firebase
                listaJugadoresPartida.value = listaJugadoresPartida.value.toMutableList().apply {
                    add(
                        Jugador(
                            jugadorId = jugador.jugadorId ?: 0,
                            jugadorName = jugador.jugadorName ?: "",
                            jugadorPoints = jugador.jugadorPoints ?: 0,
                            jugadorTotalPoints = jugador.jugadorTotalPoints ?: 0,
                            jugadorTotalGames = jugador.jugadorTotalGames ?: 0
                        )
                    )
                }

                // Imprimimos un mensaje de éxito
                println("Jugador agregado a la partida exitosamente.")
            }
                }else {
                println("El documento del usuario no existe.")
            }
        }.addOnFailureListener { exception ->
            println("Error al cargar el documento del usuario: ${exception.message}")
        }
    }

    // Eliminar un jugador de la partida
    fun eliminarJugadorDePartida(jugador: Jugador) {
        listaJugadoresPartida.value = listaJugadoresPartida.value.filterNot { it.jugadorId == jugador.jugadorId }
        println("Jugador eliminado de la partida: ${jugador.jugadorName}")
    }

    // Borrar jugador de Firebase
    // Función para eliminar un jugador de la lista de jugadores del usuario
    fun eliminarJugador(userEmail: String, jugadorName: String, jugadorPoints: Int) {
        val usuarioRef = db.collection("users").document(userEmail)

        // Obtenemos el documento del usuario para acceder a su lista de jugadores
        usersCollection.document(userEmail).get().addOnSuccessListener { document ->
                    if (document.exists()) {
                    // Obtenemos la lista actual de jugadores
                    val jugadores = document.get("jugadores") as? MutableList<Map<String, Any>>
                        ?: mutableListOf()

                    // Buscar el jugador con el nombre y puntos proporcionados
                    val jugadorAEliminar = jugadores.find {
                        it["jugadorName"].toString() == jugadorName && it["jugadorPoints"].toString().toInt() == jugadorPoints
                    }

                    if (jugadorAEliminar != null) {
                        // Eliminar el jugador encontrado
                        jugadores.remove(jugadorAEliminar)

                        // Actualizar la lista de jugadores en el documento del usuario
                        usuarioRef.update("jugadores", jugadores)
                            .addOnSuccessListener {
                                println("Jugador eliminado exitosamente.")
                            }
                            .addOnFailureListener { exception ->
                                println("Error al eliminar el jugador: ${exception.message}")
                            }
                        cargarJugadores(userEmail)
                    } else {
                        println("No se encontró ningún jugador con ese nombre y puntos.")
                    }
                } else {
                    println("El documento del usuario no existe.")
                }
            }
            .addOnFailureListener { exception ->
                println("Error al obtener el documento: ${exception.message}")
            }
    }

    // Añadir nuevo jugador a Firebase
    fun agregarJugador(userEmail: String, nuevoJugadorName: String) {
        val usuarioRef = db.collection("users").document(userEmail)

        // Primero, obtenemos el documento del usuario para leer la lista actual de jugadores
        usersCollection.document(userEmail).get().addOnSuccessListener { document ->

                if (document.exists()) {
                    // Obtenemos la lista actual de jugadores
                    val jugadores = document.get("jugadores") as? MutableList<Map<String, Any>>
                        ?: mutableListOf()

                    // Crear un nuevo jugador con los valores iniciales
                    val nuevoJugador = mapOf(
                        "jugadorId" to jugadores.size, // El ID es el tamaño actual de la lista
                        "jugadorName" to nuevoJugadorName,
                        "jugadorPoints" to 0,
                        "jugadorTotalPoints" to 0,
                        "jugadorTotalGames" to 0
                    )

                    // Añadir el nuevo jugador a la lista
                    jugadores.add(nuevoJugador)

                    // Actualizar el documento del usuario con la nueva lista de jugadores
                    usuarioRef.update("jugadores", jugadores)
                        .addOnSuccessListener {
                            println("Jugador agregado exitosamente.")
                        }
                        .addOnFailureListener { exception ->
                            println("Error al agregar el jugador: ${exception.message}")
                        }
                    cargarJugadores(userEmail)
                } else {
                    println("El documento del usuario no existe.")
                }
            }
            .addOnFailureListener { exception ->
                println("Error al obtener el documento: ${exception.message}")
            }
    }
}


/*
class AddPlayerScreenViewModel: ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _loading = MutableLiveData(false) //Esto impide que se creen varios usuarios accidentalmente

    fun signInWithGoogleCredential(credential: AuthCredential, home: () -> Unit) = //TODO cambiar home
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task -> //Si el registro es exitoso
                        if (task.isSuccessful) {
                            Log.d("LoginScreen", "Logueado con Google")
                            home()
                        } else {
                            Log.d(
                                "LoginScreen",
                                "Fallo al logearse con Google: ${task.result.toString()}"
                            )
                        }
                    }
            }catch (ex: Exception){
                Log.d("LoginScreen", "Fallo al logearse con Google: ${ex.message}")

            }
        }

    fun signInWithEmailAndPassword(email:String,password: String,home: () -> Unit) =
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("LoginScreen", "Logueado con Email")
                            home()
                        } else {
                            Log.d(
                                "LoginScreen",
                                "Fallo al logearse con Email: ${task.result.toString()}"
                            )
                        }
                    }
            }catch (ex: Exception){
                Log.d("LoginScreen", "Fallo al logearse con Email: ${ex.message}")
            }

        }

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        name: String,
        home: () -> Unit
    ){
        if(_loading.value == false){ //Esto es para que no vuelva a entrar accidentalmente y cree muchos usuarios
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val displayName = task.result?.user?.email?.split('@')?.get(0)
                        createUser(displayName) //TODO cambiar esto por name
                        home()
                    } else {
                        Log.d("Login", "createUserWithEmailAndPassword: ${task.result.toString()}")
                    }
                    _loading.value = false
                }
        }
    }

    private fun createUser(displayName: String?) {
        val userId = auth.currentUser?.uid //Sacamos el id del auth

        val user = User(
            userId = userId.toString(),
            userName = displayName.toString(),
            userPoints = "0"
        ).toMap()

        FirebaseFirestore.getInstance().collection("users").add(user).addOnSuccessListener {
            Log.d("Login", "Creado ${it.id}")
        }
            .addOnFailureListener {
                Log.d("Login", "Ocurrió un error: ${it}")
            }
    }

}

*/

