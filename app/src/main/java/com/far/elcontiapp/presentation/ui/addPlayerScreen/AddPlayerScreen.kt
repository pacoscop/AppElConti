package com.far.elcontiapp.presentation.ui.addPlayerScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text

import androidx.compose.ui.unit.dp
import com.far.elcontiapp.presentation.viewmodel.AddPlayerScreenViewModel


import androidx.compose.foundation.Image
import androidx.compose.foundation.background

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*



import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.far.elcontiapp.R
import com.far.elcontiapp.data.repository.JugadoresRepository
import com.far.elcontiapp.presentation.navigation.Screens
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)

// Composable de la pantalla
@Composable
fun AddPlayerScreen(navController: NavController, viewModel: AddPlayerScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    viewModel.obtenerJugadorConIdCero()
    val listaJugadoresPartida by viewModel.listaJugadoresPartida
    val listaJugadoresExistentes by viewModel.listaJugadoresExistentes
    val jugadorSeleccionado by viewModel.jugadorSeleccionado
    val mostrarConfirmacionBorrar by viewModel.mostrarConfirmacionBorrar
    var userEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
//    val scrollStatePlayersGame = rememberLazyListState()
//    val scrollStateAddPlayer = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.cargarJugadores(userEmail)
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    JugadoresRepository.listaJugadoresPartida.clear()
                    JugadoresRepository.listaJugadoresPartida.addAll(listaJugadoresPartida)
                    navController.navigate(Screens.RulesScreen.name)
                },
                modifier = Modifier.padding(16.dp)

            ) {
                Box(contentAlignment = Alignment.Center ){// Centra todo el contenido
                    Text("Comenzar Partida", Modifier.height(20.dp)) // Cambia el contenido del botón flotante
                }

            }
        },
        content = { innerPadding ->


            Box(modifier = Modifier.padding(innerPadding)) {
                Image(
                    painter = painterResource(id = R.drawable.anadir_jugadores), // Reemplaza con tu recurso de imagen
                    contentDescription = "Imagen de fondo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop // Ajusta cómo se escala la imagen
                )

                Column(modifier = Modifier.fillMaxSize().padding(25.dp)) {

                    // Botón para volver
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver", modifier = Modifier.size(24.dp))
                    }

                    Spacer(modifier = Modifier.height(60.dp))

                    // Título
//            Text(
//                text = "Añadir jugadores",
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.align(Alignment.CenterHorizontally)
//            )

                    Spacer(modifier = Modifier.height(125.dp))

                    // Lista de jugadores para la partida
                    Text(
                        "Lista de jugadores para la partida",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Box(
                        modifier = Modifier.border(2.dp, Color.Black)
                            .fillMaxWidth()
                            .heightIn(min = 150.dp, max = 150.dp)
                            .background(Color.White)//Altura máxima
                    ) {
                        LazyColumn (modifier = Modifier.background(Color.White)){

                            items(listaJugadoresPartida) { jugador ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(8.dp).clickable {
                                        viewModel.jugadorSeleccionado.value = jugador
                                    },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = jugador.jugadorName.toString().trim(),
                                        modifier = Modifier.weight(1f),
                                        style = TextStyle(fontSize = 20.sp)
                                    )
                                    if (jugadorSeleccionado == jugador) {
                                        IconButton(onClick = {
                                            viewModel.eliminarJugadorDePartida(
                                                jugador
                                            )
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Eliminar",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }



                    Spacer(modifier = Modifier.height(16.dp))
                    val showJugadorYaEnPartidaDialog = remember { mutableStateOf(false) }
                    // Lista de jugadores existentes
                    Text(
                        "Añadir jugador existente",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Nombre", Modifier.padding(end = 100.dp))
                        Text("Puntos", Modifier.padding(end = 30.dp))
                        Text("NºPartidas")
                    }

                    //Le aplicamos un Box para cambiar el color de fondo

                    LazyColumn(modifier = Modifier.height(150.dp).border(2.dp, Color.Black)
                        .background(Color.White)) {
                        items(listaJugadoresExistentes) { jugador ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp).clickable {
                                    viewModel.jugadorSeleccionado.value = jugador
                                },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = jugador.jugadorName.toString(),
                                    modifier = Modifier.weight(3f).padding(end = 50.dp)
                                )
                                Text(
                                    text = jugador.jugadorTotalPoints.toString(),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = jugador.jugadorTotalGames.toString(),
                                    modifier = Modifier.weight(1f)
                                )
                                if (jugadorSeleccionado == jugador) {
                                    IconButton(onClick = {
                                        viewModel.agregarJugadorAPartida(userEmail, jugador) {
                                            // Mostramos el mensaje si el jugador ya está en la partida
                                            showJugadorYaEnPartidaDialog.value = true
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Upgrade,
                                            contentDescription = "Agregar a partida",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    IconButton(onClick = {
                                        viewModel.mostrarConfirmacionBorrar.value = true
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Borrar",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    //Mensaje si el jugador ya está en la partida


                    if (showJugadorYaEnPartidaDialog.value) {
                        AlertDialog(
                            onDismissRequest = { showJugadorYaEnPartidaDialog.value = false },
                            confirmButton = {
                                Button(onClick = { showJugadorYaEnPartidaDialog.value = false }) {
                                    Text("Aceptar")
                                }
                            },
                            title = { Text("Jugador ya en la partida") },
                            text = { Text("Este jugador ya está en la lista de la partida.") }
                        )
                    }


                    // Confirmación de borrado
                    if (mostrarConfirmacionBorrar && jugadorSeleccionado != null) {
                        AlertDialog(
                            onDismissRequest = {
                                viewModel.mostrarConfirmacionBorrar.value = false
                            },
                            title = { Text("Confirmar borrado") },
                            text = { Text("¿Estás seguro que quieres borrar a ${jugadorSeleccionado?.jugadorName}?") },
                            confirmButton = {
                                Button(onClick = {
                                    jugadorSeleccionado?.let {
                                        viewModel.eliminarJugador(
                                            userEmail,
                                            it.jugadorName.toString(),
                                            it.jugadorPoints.toString().toInt()
                                        )
                                    }
                                    viewModel.mostrarConfirmacionBorrar.value = false
                                }) {
                                    Text("Aceptar")
                                }
                            },
                            dismissButton = {
                                Button(onClick = {
                                    viewModel.mostrarConfirmacionBorrar.value = false
                                }) {
                                    Text("Cancelar")
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Añadir nuevo jugador
                    Text("Añadir nuevo jugador", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        var nuevoJugadorNombre by remember { mutableStateOf("") }
                        TextField(
                            value = nuevoJugadorNombre,
                            onValueChange = { nuevoJugadorNombre = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Nombre") }
                        )
                        IconButton(onClick = {
                            viewModel.agregarJugador(
                                userEmail,
                                nuevoJugadorNombre
                            )
                        }) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Añadir")
                        }
                    }

                    Spacer(modifier = Modifier.height(60.dp))

                    // Botón para comenzar partida
//            Button(
//                onClick = {
//                    JugadoresRepository.listaJugadoresPartida.clear()
//                    JugadoresRepository.listaJugadoresPartida.addAll(listaJugadoresPartida)
//                    navController.navigate(Screens.RulesScreen.name)
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 16.dp)
//            ) {
//                Text("Comenzar partida")
//            }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("")
                    }
                }
            }
        }
            )
}




    /*
@Preview(showBackground = true)
@Composable
fun AddPlayerScreenPreview() {
    AddPlayerScreen(navController = rememberNavController(), viewModel = AddPlayerScreenViewModel())
}*/