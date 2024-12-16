package com.far.elcontiapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.far.elcontiapp.data.model.Jugador
import com.far.elcontiapp.data.model.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateQuery
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class LoginScreenViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val _loading = MutableLiveData(false)
    val errorMessage = MutableLiveData("")

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

    fun signInWithEmailAndPassword(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        errorMessage.value = ""
                        onResult(true)
                    } else {
                        errorMessage.value = "Correo o contraseña incorrectos"
                        onResult(false)
                    }
                }
                .addOnFailureListener { ex ->
                    errorMessage.value = ex.localizedMessage ?: "Error desconocido"
                    onResult(false)
                }
        }
    }

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        name: String,
        points: Int = 0,
        pointsTotales: Int = 0,
        partidasTotales: Int = 0,
        onResult: (Boolean) -> Unit
    ) {
        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        errorMessage.value = ""
                        val jugador = Jugador(
                            jugadorId = 0,
                            jugadorName = email,
                            jugadorPoints = points,
                            jugadorTotalPoints = pointsTotales,
                            jugadorTotalGames = partidasTotales
                        )

                        val user = User(
                            userId = auth.currentUser?.uid,
                            userEmail = email,
                            jugadores = listOf(jugador)

                        )
                        //user.inicializarJugadorId()
                        val userMap = user.toMap()
                        createUser(userMap)
                        onResult(true)
                    } else {
                        errorMessage.value = "Error al crear la cuenta"
                        onResult(false)
                    }
                    _loading.value = false
                }
                .addOnFailureListener { ex ->
                    errorMessage.value = ex.localizedMessage ?: "Error desconocido"
                    _loading.value = false
                    onResult(false)
                }
        }
    }

    private fun createUser(userMap: MutableMap<String, *>) {
        //val userId = auth.currentUser?.uid
        //val user = User(userId ?: "", name, "0").toMap()

        var countQuery = FirebaseFirestore.getInstance().collection("users").count()
        var count: Int = 0
        //Obtener Int de número de users, no furula
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                count = snapshot.count.toInt()
                Log.d("AggregateQuery","Número de documentos: $count")
            } else {
                Log.e("AggregateQuery", "Error al contar documentos", task.exception)
            }

        }
        FirebaseFirestore.getInstance().collection("users")
            .document(userMap["userEmail"].toString())
            .set(userMap)


            /*.add(user)
            .addOnSuccessListener {
                Log.d("Login", "Usuario creado con ID: ${it.id}")
            }
            .addOnFailureListener {
                Log.d("Login", "Error al crear usuario: $it")
            }*/
    }

    /*
    fun isNamed(): Boolean {
        FirebaseFirestore.getInstance().collection("users").
    }
    */

}


/*
class LoginScreenViewModel: ViewModel() {

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