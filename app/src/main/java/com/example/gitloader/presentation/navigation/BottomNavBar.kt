package com.example.gitloader.presentation.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.gitloader.R

@Composable
fun BottomBarNav(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    class BottomNavScreen(
        val iconSelected: Int,
        val iconUnselected: Int,
        val contentDescription: String,
        val destination: String,
    )

    val screens = arrayListOf(
        BottomNavScreen(
            iconSelected = R.drawable.ic_search_selected,
            iconUnselected = R.drawable.ic_search,
            contentDescription = "Поиск",
            destination = SearchDestination
        ),

        BottomNavScreen(
            iconSelected = R.drawable.ic_folder_selected,
            iconUnselected = R.drawable.ic_folder,
            contentDescription = "Загрузки",
            destination = DownloadsDestination
        )
    )
    NavigationBar {
        for (screen in screens) {
            NavigationBarItem(
                selected = false,
                modifier = Modifier.height(36.dp),
                icon = {
                    if (currentRoute == screen.destination) {
                        Image(
                            painterResource(id = screen.iconSelected),
                            contentDescription = screen.contentDescription
                        )
                    } else {
                        Image(
                            painterResource(id = screen.iconUnselected),
                            contentDescription = screen.contentDescription
                        )
                    }
                },
                onClick = {
                    navController.navigate(screen.destination) {

                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true

                    }
                },
                label = { Text(screen.contentDescription) }
            )
        }
    }
}