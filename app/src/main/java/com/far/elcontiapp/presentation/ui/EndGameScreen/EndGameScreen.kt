package com.far.elcontiapp.presentation.ui.EndGameScreen

import GameViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.far.elcontiapp.data.repository.JugadoresRepository
import com.far.elcontiapp.presentation.navigation.Screens
import com.far.elcontiapp.R
import com.far.elcontiapp.presentation.viewmodel.EndGameScreenViewModel

@Composable
fun EndGameScreen(
    navController: NavController,
    viewModel: EndGameScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    // Estado de los jugadores
    val jugadores = JugadoresRepository.listaJugadoresPartida

    Box(modifier = Modifier.fillMaxSize())
    {
        Image(
            painter = painterResource(id = R.drawable.pantalla_resultados),
            contentDescription = "Imagen de fondo",
            modifier = Modifier.fillMaxSize(1f),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(150.dp))
            // Relleno de resultados
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 300.dp), // Ocupa el espacio disponible
                shape = RoundedCornerShape(12.dp),
                //elevation = 4.dp,
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                if (jugadores.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay jugadores disponibles")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        //Aquí ordeno mis jugadores
                        val jugadoresOrdenados = jugadores.sortedByDescending { it.jugadorPoints }
                        items(jugadoresOrdenados) { jugador ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                jugador.jugadorName?.let {
                                    Text(
                                        text = it,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(text = jugador.jugadorPoints.toString())
                            }
                            Divider()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para jugar otra partida
            Row(modifier = Modifier.fillMaxWidth()) {


                Button(
                    onClick = {
                        viewModel.resetListaJugadores() // Vaciamos la lista de jugadores
                        navController.navigate(Screens.AddPlayerScreen.name) // Navegamos a AddPlayerScreen
                    },
                    modifier = Modifier.fillMaxWidth(0.5f).height(200.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Cyan, // Color del botón
                        contentColor = Color.Black // Color del texto o contenido dentro del botón
                    )
                ) {
                    Text("Jugar otra partida", style = TextStyle(fontSize = 30.sp))
                }
                Spacer(modifier = Modifier.width(10.dp))

                Button(onClick = {
                    viewModel.resetListaJugadores() // Vaciamos la lista de jugadores
                    navController.navigate(Screens.LoginScreen.name) // Navegamos a AddPlayerScreen
                },
                    modifier = Modifier.fillMaxWidth(1f).height(200.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Yellow, // Color del botón
                        contentColor = Color.Black // Color del texto o contenido dentro del botón
                    )
                )
                {
                    Text("Cambiar de usuario", style = TextStyle(fontSize = 30.sp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button( onClick = {
                System.exit(0)
            },
                modifier = Modifier.fillMaxWidth(1f).height(60.dp),
                shape = RoundedCornerShape(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red, // Color del botón
                    contentColor = Color.White // Color del texto o contenido dentro del botón
                )
            )
            {
                Text("Salir de la App", style = TextStyle(fontSize = 20.sp))
            }
        }
    }
}