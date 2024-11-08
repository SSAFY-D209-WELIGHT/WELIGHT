package com.rohkee.welight.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController = navController, startDestination = Home) {
        composable<Login> {
        }

        composable<Home> {
            BottomTabSubNavigation()
        }

        composable<DisplayDetail> {
        }

        composable<DisplayEditor> {
        }
    }
}
