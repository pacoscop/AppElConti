package com.far.elcontiapp.presentation.navigation

import GameScreen
import RulesScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.far.elcontiapp.presentation.ui.CreateName.CreateNameScreen
import com.far.elcontiapp.presentation.ui.EndGameScreen.EndGameScreen
import com.far.elcontiapp.presentation.ui.SplashScreen.SplashScreen


import com.far.elcontiapp.presentation.ui.addPlayerScreen.AddPlayerScreen
import com.far.elcontiapp.presentation.ui.screenLogin.LoginScreen

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screens.SplashScreen.name) {
        composable(Screens.LoginScreen.name) { LoginScreen(navController) }
        composable(Screens.AddPlayerScreen.name) { AddPlayerScreen(navController) }
        composable(Screens.CreateNameScreen.name) { CreateNameScreen(navController) }
        composable(Screens.RulesScreen.name) { RulesScreen(navController) }
        composable(Screens.GameScreen.name){GameScreen(navController)}
        composable(Screens.EndGameScreen.name){EndGameScreen(navController)}
        composable(Screens.SplashScreen.name){SplashScreen(navController)}
    }
}



