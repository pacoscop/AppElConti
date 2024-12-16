package com.far.elcontiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.far.elcontiapp.presentation.navigation.AppNavHost
import com.far.elcontiapp.presentation.ui.screenLogin.LoginScreen

import com.far.elcontiapp.ui.theme.ElContiAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ElContiAppTheme {
                val navController = rememberNavController()
                AppNavHost(navController)
            }
        }
    }
}