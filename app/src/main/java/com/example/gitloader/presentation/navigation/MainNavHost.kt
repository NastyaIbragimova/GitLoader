package com.example.gitloader.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.gitloader.presentation.downloads.DownloadsScreen
import com.example.gitloader.presentation.search.SearchScreen
import com.example.gitloader.presentation.splash.SplashScreen

@Composable
fun MainContent(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val showBottomBar =
        when (navBackStackEntry?.destination?.route) {
            SplashDestination -> false
            else -> true
        }

    Scaffold(
        bottomBar = { if (showBottomBar) BottomBarNav(navController = navController) },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = SplashDestination,
            modifier = Modifier.padding(padding)
        ) {
            splashContent(navController)
            searchContent()
            downloadsContent()
        }
    }
}

private fun NavGraphBuilder.splashContent(navController: NavHostController) {
    composable(route = SplashDestination) { _ ->
        SplashScreen(main = {
            navController.navigate(SearchDestination) {
                launchSingleTop = true
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = false
                }
            }
        })
    }
}

private fun NavGraphBuilder.searchContent() {
    composable(route = SearchDestination) { _ ->
        SearchScreen()
    }
}

private fun NavGraphBuilder.downloadsContent() {
    composable(route = DownloadsDestination) { _ ->
        DownloadsScreen()
    }
}