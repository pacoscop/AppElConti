package com.far.elcontiapp.presentation.ui.screenLogin

import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.far.elcontiapp.R
import com.far.elcontiapp.presentation.navigation.Screens
import com.far.elcontiapp.presentation.viewmodel.LoginScreenViewModel
import com.far.elcontiapp.ui.theme.Black
import com.far.elcontiapp.ui.theme.ElContiAppTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.layout.ContentScale

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val showLoginForm = rememberSaveable { mutableStateOf(true) }
    val errorMessage = viewModel.errorMessage.observeAsState("")
    val token = "719910361900-ocrtakjtptdbe8947dd49i10sk7s6m8h.apps.googleusercontent.com"
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts
            .StartActivityForResult() //Esto abrirá un activity para hacer el login de google
    ){
        val task =
            GoogleSignIn.getSignedInAccountFromIntent(it.data) //esto facilita la librería añadida, el intent será enviado cuando se lance el launcher
        try{
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            viewModel.signInWithGoogleCredential(credential){
                navController.navigate(Screens.AddPlayerScreen.name) //TODO
            }
        } catch (ex: Exception){
            Log.d("LoginScreen", "Error: ${ex.message}")
        }
    }

    Box( modifier = Modifier
        .fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo_conti), // Reemplaza con tu recurso de imagen
            contentDescription = "Imagen de fondo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Ajusta cómo se escala la imagen
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                    //.background(color = MaterialTheme.colorScheme.background), Lo activamos si quereos background color pero avamos a usar una imagen
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
               //  Text("El Conti", color = MaterialTheme.colorScheme.onBackground) Texto incluido en la imagen
                Spacer(Modifier.height(100.dp))

                Column(modifier = Modifier.padding(start = 5.dp)) {
                    if (showLoginForm.value) {
                        Text(
                            text = "Inicia sesión",
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        UserForm(isCreateAccount = false) { email, password ->
                            viewModel.signInWithEmailAndPassword(
                                email,
                                password
                            ) { success ->
                                if (success) {
                                    navController.navigate(Screens.AddPlayerScreen.name)
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Crear cuenta",
                            Modifier.fillMaxWidth(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        UserForm(isCreateAccount = true) { email, password ->
                            viewModel.createUserWithEmailAndPassword(
                                email,
                                password,
                                name = ""
                            ) { success ->
                                if (success) {
                                    navController.navigate(Screens.AddPlayerScreen.name)
                                }
                            }
                        }
                    }
                }

                if (errorMessage.value.isNotEmpty()) {
                    Text(
                        text = errorMessage.value,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val text1 =
                        if (showLoginForm.value) "¿No tienes cuenta?" else "¿Ya tienes cuenta?"
                    val text2 = if (showLoginForm.value) "Regístrate" else "Inicia sesión"
                    Text(
                        text = text1,
                        Modifier,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = text2,
                        modifier = Modifier
                            .clickable { showLoginForm.value = !showLoginForm.value }
                            .padding(start = 5.dp),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                //GOOGLE
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            //Se crea un builder de opciones, una de ellas incluye el token
                            val opciones = GoogleSignInOptions
                                .Builder(
                                    GoogleSignInOptions.DEFAULT_SIGN_IN
                                )
                                .requestIdToken(token) //requiere el token
                                .requestEmail() //y tambien requiere el email
                                .build()
                            //creamos un cliente de logueo con estas opciones
                            val googleSignInCliente = GoogleSignIn.getClient(context, opciones)
                            launcher.launch(googleSignInCliente.signInIntent)
                        },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.el_logo_g_de_google),
                        contentDescription = "Login con Google",
                        modifier = Modifier
                            .padding(10.dp)
                            .size(40.dp)
                            .height(20.dp)
                    )
                    Text(
                        text = "Login con Google",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }

}

@Composable
fun UserForm(
    isCreateAccount: Boolean,
    onDone: (String, String) -> Unit = { email, pwd -> }
) {
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisible = rememberSaveable { mutableStateOf(false) }
    val valido = remember(email.value, password.value) {
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
    }

    //Controla que al hacer click en el boton submit, el teclado se oculte
    val keyboardController = LocalSoftwareKeyboardController.current

    // Surface (modifier = Modifier.fillMaxSize()){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailInput(emailState = email)
        PasswordInput(
            passwordState = password,
            passwordVisible = passwordVisible
        )
        SubmitButton(
            textId = if (isCreateAccount) "Crear cuenta" else "Login",
            inputValido = valido
        ) {
            onDone(email.value.trim(), password.value.trim())
            //se oculta el teclado, el ? es que se llama a la función en modo seguro
            keyboardController?.hide()
        }

    }

    //}

}


@Composable
fun SubmitButton(
    textId: String,
    inputValido: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp),
        shape = CircleShape,
        enabled = inputValido
    ) {
        Text(
            text = textId,
            modifier = Modifier.padding(5.dp)
        )
    }
}

@Composable
fun EmailInput(
    emailState: MutableState<String>,
    labelId: String = "Email"
) {
    InputField(
        valuestate = emailState,
        labelId = labelId,
        keyboardType = KeyboardType.Email,

        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    valuestate: MutableState<String>,
    labelId: String,
    keyboardType: KeyboardType,
    isSingleLine: Boolean = true
) {
    OutlinedTextField(
        value = valuestate.value,
        onValueChange = { valuestate.value = it },
        label = { Text(text = labelId, color = Color.White) },
        singleLine = isSingleLine,
        modifier = Modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedTextColor = Color.White
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordInput(
    passwordState: MutableState<String>,
    passwordVisible: MutableState<Boolean>,
    labelId: String = "Password" //El labelId sirve para poner una palabra que se muestre antes de que el usuario empiece a escribir
) {
    val visualTransformation = if (passwordVisible.value)
        VisualTransformation.None
    else PasswordVisualTransformation()

    OutlinedTextField(
        value = passwordState.value,
        onValueChange = { passwordState.value = it },
        label = { Text(text = labelId, color = Color.White) },
        singleLine = true,
        modifier = Modifier
            .padding(
                bottom = 10.dp,
                start = 10.dp,
                end = 10.dp
            )
            .fillMaxWidth(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedTextColor = Color.White
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = visualTransformation,
        trailingIcon = {
            if (passwordState.value.isNotBlank()) {
                PasswordVisibleIcon(passwordVisible)
            } else null
        }
    )
}

@Composable
fun PasswordVisibleIcon(passwordVisible: MutableState<Boolean>) {
    val image = if (passwordVisible.value) {
        Icons.Default.VisibilityOff
    } else Icons.Default.Visibility

    IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
        Icon(
            imageVector = image,
            contentDescription = ""
        )
    }
}


/*
@Composable
fun LoginScreen(
    navController: NavController, //TODO
    viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel() //TODO
){
    val showLoginForm = rememberSaveable {mutableStateOf(true) }
    val token = "719910361900-ocrtakjtptdbe8947dd49i10sk7s6m8h.apps.googleusercontent.com"
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts
            .StartActivityForResult() //Esto abrirá un activity para hacer el login de google
    ){
        val task =
            GoogleSignIn.getSignedInAccountFromIntent(it.data) //esto facilita la librería añadida, el intent será enviado cuando se lance el launcher
        try{
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            viewModel.signInWithGoogleCredential(credential){
                navController.navigate(Screens.AddPlayerScreen.name) //TODO
            }
        } catch (ex: Exception){
            Log.d("LoginScreen", "Error: ${ex.message}")
        }
    }

    Surface (
        modifier = Modifier
            .fillMaxSize()
    ){
        Column(modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Row() {
                Text("El Conti",
                    color = MaterialTheme.colorScheme.onBackground )
            }
            Spacer(Modifier.height(50.dp))

            Column(modifier = Modifier.padding(start = 5.dp)){
                if (showLoginForm.value) {
                    Text(
                        text = "Inicia sesión",
                        Modifier.fillMaxWidth().padding(start = 10.dp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    UserForm(isCreateAccount = false) { email, password ->
                        Log.d("My Login", "Logueado con $email y $password")
                        viewModel.signInWithEmailAndPassword(
                            email,
                            password
                        ) {//pasamos email, password, y la funcion que navega hacia home
                            navController.navigate(Screens.AddPlayerScreen.name)
                        }
                    }
                } else {
                    Text(
                        text = "Crear cuenta",
                        Modifier.fillMaxWidth(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    UserForm(isCreateAccount = true) { email, password ->
                        Log.d("My Login", "Logueado con $email y $password")
                        viewModel.createUserWithEmailAndPassword(
                            email,
                            password,
                            name = ""
                        ) {//pasamos email, password, y la funcion que navega hacia home
                            navController.navigate(Screens.AddPlayerScreen.name)
                        }
                    }
                }
            }


            //Alternar entre Crear cuenta e iniciar sesión
            //Spacer(modifier = Modifier.height(15.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val text1 = if (showLoginForm.value) "¿No tienes cuenta?"
                else "¿Ya tienes cuenta?"
                val text2 = if (showLoginForm.value) "Regístrate"
                else "Inicia sesión"
                Text(
                    text = text1,
                    Modifier,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(text = text2,
                    modifier = Modifier
                        .clickable {
                            showLoginForm.value = !showLoginForm.value
                        }
                        .padding(start = 5.dp),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

//            //GOOGLE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        //Se crea un builder de opciones, una de ellas incluye el token
                        val opciones = GoogleSignInOptions
                            .Builder(
                                GoogleSignInOptions.DEFAULT_SIGN_IN
                            )
                            .requestIdToken(token) //requiere el token
                            .requestEmail() //y tambien requiere el email
                            .build()
                        //creamos un cliente de logueo con estas opciones
                        val googleSignInCliente = GoogleSignIn.getClient(context, opciones)
                        launcher.launch(googleSignInCliente.signInIntent)
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.el_logo_g_de_google),
                    contentDescription = "Login con Google",
                    modifier = Modifier
                        .padding(10.dp)
                        .size(40.dp)
                        .height(20.dp)
                )
                Text(
                    text = "Login con Google",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

        }

    }

}

@Composable
fun UserForm(
    isCreateAccount: Boolean,
    onDone: (String, String) -> Unit = { email, pwd -> }
) {
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisible = rememberSaveable { mutableStateOf(false) }
    val valido = remember(email.value, password.value) {
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
    }

    //Controla que al hacer click en el boton submit, el teclado se oculte
    val keyboardController = LocalSoftwareKeyboardController.current

   // Surface (modifier = Modifier.fillMaxSize()){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EmailInput(emailState = email)
            PasswordInput(
                passwordState = password,
                passwordVisible = passwordVisible
            )
            SubmitButton(
                textId = if (isCreateAccount) "Crear cuenta" else "Login",
                inputValido = valido
            ) {
                onDone(email.value.trim(), password.value.trim())
                //se oculta el teclado, el ? es que se llama a la función en modo seguro
                keyboardController?.hide()
            }

        }

    //}

}


@Composable
fun SubmitButton(
    textId: String,
    inputValido: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp),
        shape = CircleShape,
        enabled = inputValido
    ) {
        Text(
            text = textId,
            modifier = Modifier.padding(5.dp)
        )
    }
}

@Composable
fun EmailInput(
    emailState: MutableState<String>,
    labelId: String = "Email"
) {
    InputField(
        valuestate = emailState,
        labelId = labelId,
        keyboardType = KeyboardType.Email,

        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    valuestate: MutableState<String>,
    labelId: String,
    keyboardType: KeyboardType,
    isSingleLine: Boolean = true
) {
    OutlinedTextField(
        value = valuestate.value,
        onValueChange = { valuestate.value = it },
        label = { Text(text = labelId, color = Color.White) },
        singleLine = isSingleLine,
        modifier = Modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedTextColor = Color.White
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordInput(
    passwordState: MutableState<String>,
    passwordVisible: MutableState<Boolean>,
    labelId: String = "Password" //El labelId sirve para poner una palabra que se muestre antes de que el usuario empiece a escribir
) {
    val visualTransformation = if (passwordVisible.value)
        VisualTransformation.None
    else PasswordVisualTransformation()

    OutlinedTextField(
        value = passwordState.value,
        onValueChange = { passwordState.value = it },
        label = { Text(text = labelId, color = Color.White) },
        singleLine = true,
        modifier = Modifier
            .padding(
                bottom = 10.dp,
                start = 10.dp,
                end = 10.dp
            )
            .fillMaxWidth(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedTextColor = Color.White
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = visualTransformation,
        trailingIcon = {
            if (passwordState.value.isNotBlank()) {
                PasswordVisibleIcon(passwordVisible)
            } else null
        }
    )
}

@Composable
fun PasswordVisibleIcon(passwordVisible: MutableState<Boolean>) {
    val image = if (passwordVisible.value) {
        Icons.Default.VisibilityOff
    } else Icons.Default.Visibility

    IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
        Icon(
            imageVector = image,
            contentDescription = ""
        )
    }
}

//
//@Preview(showBackground = true, showSystemUi = true)
//@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//fun LoginScreenPreview(){
//    ElContiAppTheme {
//        LoginScreen(
//            navController = rememberNavController(),
//        viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
//        )
//    }
//
//}

*/