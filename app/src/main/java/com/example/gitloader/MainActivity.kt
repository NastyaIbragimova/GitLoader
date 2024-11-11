package com.example.gitloader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.gitloader.presentation.navigation.MainContent
import com.example.gitloader.presentation.theme.GitLoaderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GitLoaderTheme {
                MainActivityContent()
            }
        }
    }
}

@Composable
private fun MainActivityContent() {
    val navController = rememberNavController()
    MainContent(navController = navController)
}