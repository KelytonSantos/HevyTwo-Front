package com.example.sort.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.sort.CreateRoutineScreen
import com.example.sort.DashboardScreen
import com.example.sort.EditRoutineScreen
import com.example.sort.ExploreExerciseScreen
import com.example.sort.LoginScree
import com.example.sort.MenuWorkoutScreen
import com.example.sort.MyRoutinesScreen
import com.example.sort.data.SessionManager
import com.example.sort.viewmodel.RoutineBuilderViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val routineBuilderViewModel: RoutineBuilderViewModel = viewModel()

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
                onMyWorkouts = {
                    navController.navigate("my_routines")
                },
                onCreateWorkout = {
                    routineBuilderViewModel.reset()
                    navController.navigate("create_routine")
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("my_routines") {
            MyRoutinesScreen(
                onBack = { navController.popBackStack() },
                onCreateRoutine = {
                    routineBuilderViewModel.reset()
                    navController.navigate("create_routine")
                },
                onRoutineClick = { routineId, routineName ->
                    val encoded = java.net.URLEncoder.encode(routineName, "UTF-8")
                    navController.navigate("edit_routine/$routineId/$encoded")
                }
            )
        }
        // Explorar exercicios sem modo de selecao
        composable("explore_exercises") {
            ExploreExerciseScreen(
                selectionMode = false,
                onBack = { navController.popBackStack() }
            )
        }
        // Selecionar exercicios para uma nova rotina
        composable("select_exercises") {
            ExploreExerciseScreen(
                selectionMode = true,
                routineBuilderViewModel = routineBuilderViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("create_routine") {
            CreateRoutineScreen(
                routineBuilderViewModel = routineBuilderViewModel,
                onBack = { navController.popBackStack() },
                onAddExercises = { navController.navigate("select_exercises") },
                onSaveSuccess = {
                    navController.navigate("menu_workout") {
                        popUpTo("menu_workout") { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = "edit_routine/{routineId}/{routineName}",
            arguments = listOf(
                navArgument("routineId") { type = NavType.StringType },
                navArgument("routineName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId") ?: ""
            val routineName = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("routineName") ?: "", "UTF-8"
            )
            EditRoutineScreen(
                routineId = routineId,
                routineName = routineName,
                onBack = { navController.popBackStack() },
                onAddExercises = { navController.navigate("select_exercises") }
            )
        }
    }
}
