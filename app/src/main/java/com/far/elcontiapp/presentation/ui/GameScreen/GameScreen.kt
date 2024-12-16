import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.far.elcontiapp.R
import com.far.elcontiapp.data.repository.JugadoresRepository
import com.far.elcontiapp.presentation.navigation.Screens
import com.far.elcontiapp.presentation.viewmodel.AddPlayerScreenViewModel


@Composable
fun GameScreen(navController: NavController, viewModel: GameViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    // val jugadores = viewModel.listaJugadoresPartida.collectAsState().value
    val jugadores = JugadoresRepository.listaJugadoresPartida
    val seccionesBloqueadas = viewModel.seccionesBloqueadas.collectAsState().value
    var seccionesTextos by remember {
        mutableStateOf(
            listOf(
                "1 Trio y 1 Escalera",
                "2 Trio y 1 Escalera",
                "1 Trío y 2 Escaleras",
                "3 Tríos",
                "3 Escaleras",
                "2 Tríos y 2 Escaleras",
                "4 Tríos",
                "4 Escaleras"
            )
        )
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.fondopartida),
            contentDescription = "Fondo Partida",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Ajusta cómo se escala la imagen
        )


        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            // Botones de navegación
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { navController.navigate(Screens.RulesScreen.name) }) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Reglas")
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            // Título "PARTIDA!"
            Text(
                text = "",
                fontSize = 28.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ranking de jugadores
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Black)
                    .background(Color.Yellow)
                    .padding(8.dp)
            ) {
                Column {
                    Text("Ranking", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    jugadores.sortedByDescending { it.jugadorPoints }
                        .forEachIndexed { index, jugador ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "${index + 1}. ${jugador.jugadorName}",
                                    modifier = Modifier.weight(1f)
                                )
                                Text("${jugador.jugadorPoints}")
                            }
                        }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            // Secciones desplegables
            LazyColumn (modifier = Modifier.background(Color.White)
                .border(2.dp, Color.Black)
                .heightIn(max = 500.dp)
                .padding(10.dp)){
                items(8) { index ->
                    var isExpanded by remember { mutableStateOf(false) }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isExpanded = !isExpanded },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(index / 2 + 2) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = seccionesTextos[index],
                                style = TextStyle(fontSize = 16.sp),
                                //modifier = Modifier
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = { viewModel.cambiarEstadoSeccion(index) }) {
                                Icon(
                                    imageVector = if (seccionesBloqueadas[index]) Icons.Default.Lock else Icons.Default.LockOpen,
                                    contentDescription = null
                                )
                            }
                        }
                        if (isExpanded) {
                            // Tabla de jugadores y puntos
                            jugadores.forEach { jugador ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    jugador.jugadorName?.let {
                                        Text(
                                            it,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    if (seccionesBloqueadas[index]) {
                                        Text(
                                            "${
                                                jugador.jugadorId?.let {
                                                    viewModel.obtenerPuntosJugador(
                                                        index,
                                                        it
                                                    )
                                                }
                                            }"
                                        )
                                    } else {
                                        var puntos by remember {
                                            mutableStateOf(
                                                jugador.jugadorId?.let {
                                                    viewModel.obtenerPuntosJugador(
                                                        index,
                                                        it
                                                    ).toString()
                                                }
                                            )
                                        }
                                        puntos?.let {
                                            BasicTextField(
                                                value = it,
                                                onValueChange = {
                                                    if (it.toIntOrNull() != null) {
                                                        puntos = it
                                                        jugador.jugadorId?.let { it1 ->
                                                            viewModel.actualizarPuntosJugador(
                                                                index,
                                                                it1, it.toInt()
                                                            )
                                                        }
                                                    }
                                                },
                                                textStyle = TextStyle(fontSize = 16.sp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón "Terminar partida"
            Button(
                onClick = {
                    // Recorre todos los jugadores y actualiza sus puntos en Firebase
                    JugadoresRepository.listaJugadoresPartida.forEach { jugador ->
                        jugador.jugadorId?.let {
                            jugador.jugadorPoints?.let { it1 ->
                                viewModel.actualizarPuntosFirebase() { success ->
                                    if (!success) {
                                        // Manejar error si la actualización falla
                                        Log.e(
                                            "GameViewModel",
                                            "Error al actualizar puntos del jugador: ${jugador.jugadorId}"
                                        )
                                    }
                                }
                            }
                        }
                    }
                    // Navegar a la pantalla de finalización de partida
                    navController.navigate(Screens.EndGameScreen.name)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Terminar Partida")
            }
        }
    }
}
