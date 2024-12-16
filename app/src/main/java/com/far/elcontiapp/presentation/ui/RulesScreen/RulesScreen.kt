import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController

import com.far.elcontiapp.R
import com.far.elcontiapp.presentation.navigation.Screens

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RulesScreen(navController: NavController) {
    var showDialog by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()


//    Box(modifier = Modifier.fillMaxSize()) {
//        Image(
//            painter = painterResource(id = R.drawable.pantallareglas),
//            contentDescription = "Imagen de fondo",
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop
//        )


        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Mensaje") },
                text = {
                    Text(
                        "Con esta aplicación podéis jugar con las normas que queráis tan solo tenéis que registrar los puntos de cada ronda cuando acaben. Aquí tenéis las reglas del conti, el orden de las rondas y las distintas modificaciones según las normas que solemos usar en la modalidad de Ana o en la modalidad \"normal\" así como un apartado de reglas, podéis consultarlas antes de entrar a la partida."
                    )
                },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Aceptar")
                    }
                }
            )
        }

        Scaffold(
            topBar = {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.arrow_up_float),
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
            },
            bottomBar = {
                Button(
                    onClick = { navController.navigate(Screens.GameScreen.name) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Comenzar partida")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = "REGLAS",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Rules Sections
                ExpandableSection(title = "Reglas", content = TextReglas())
                ExpandableSection(title = "Orden de partidas", content = TextOrdenPartidas())
                ExpandableSection(title = "Normas Ana", content = TextNormasAna())
                ExpandableSection(title = "Normas Normales", content = TextNormasNormales())
            }
        }
    }

    @Composable
    fun ExpandableSection(title: String, content: String) {
        var expanded by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ClickableText(
                text = AnnotatedString(title),
                onClick = { expanded = !expanded },
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            if (expanded) {
                Text(
                    text = content,
                    modifier = Modifier.padding(vertical = 8.dp),
                    fontSize = 16.sp
                )
            }
            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
   // }
}

fun TextReglas(): String {
    return("Aquí está el texto de las reglas")
}

fun TextOrdenPartidas(): String {
    return("Aquí está el texto de la orden de partidas")

}

fun TextNormasAna(): String {
    return("Aquí está el texto de las normas Ana." +
            "ddddddddddddddddddddddddddddddddddddddddddddddddddddddd" +
            "dddddddddddddddddddddddddddddddddddddddddddddddddddddddd" +
            "kjhefñcwjfqwkfjcmqkwcmqwkjmqcwñjqmkjfñcñqqkwcjmqkñwejmcjqc" +
            "kjdglksjfmkjmfasdjfmasj akldsfj alksjfasdlkjfaslkdjf aklsjfask" +
            "lsdkfmaclkcjfñksljgkslfgjlkjgkrjagñlkdfjgkafdjgkadfjgdfjmgakdfjg" +
            "lñdksjfskdjñkaljgn añkjgñskdjgn ñdjñsdffñajfdh gañ gha")

}

fun TextNormasNormales(): String {
    return("Aquí está el texto de las normas normales")
}