package com.example.sort.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sort.DashboardScreen
import com.example.sort.ExploreExerciseScreen
import com.example.sort.LoginScree
import com.example.sort.MenuWorkoutScreen
import com.example.sort.data.SessionManager

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // Coleta o token do DataStore como um estado do Compose
    val token by sessionManager.jwtToken.collectAsState(initial = "loading")

    // Enquanto estiver lendo do disco, não mostramos nada (ou uma tela de loading)
    if (token == "loading") {
        return
    }

    NavHost(
        navController = navController,
        // Se o token não for nulo, começa na dashboard, senão no login
        startDestination = if (token != null) "dashboard" else "login"
    ) {
        composable("login") {
            LoginScree(onLoginSuccess = {
                navController.navigate("dashboard") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }
        composable("dashboard") {
            DashboardScreen(
                onWorkoutClick = {
                    // CORREÇÃO: O nome da rota deve ser exatamente igual ao definido no composable
                    navController.navigate("menu_workout")
                }
            )
        }
        composable("menu_workout") {
            MenuWorkoutScreen(
                onExploreClick = {
                    navController.navigate("explore_exercises")
                },
                onBack = { navController.popBackStack() }
            )
        }
        // Adicionando a rota de explorar para evitar crash ao clicar no ExploreCard
        composable("explore_exercises") {
            ExploreExerciseScreen(onBack = { navController.popBackStack() })
        }
    }
}
