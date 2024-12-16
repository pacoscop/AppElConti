package com.far.elcontiapp.presentation.navigation


sealed class Screens(val name: String) {
    object LoginScreen : Screens("login_screen")
    object AddPlayerScreen : Screens("add_player_screen")
    object RulesScreen : Screens("rules_screen")
    object CreateNameScreen : Screens("create_name_screen")
    object GameScreen : Screens("game_screen")
    object EndGameScreen : Screens("end_game_screen")
    object SplashScreen : Screens("splash_screen")
}
/*
enum class Screens {
    LoginScreen,
    AddPlayerScreen,
    RulesScreen,
    gameScreen,
    resultsScreen
}
*/