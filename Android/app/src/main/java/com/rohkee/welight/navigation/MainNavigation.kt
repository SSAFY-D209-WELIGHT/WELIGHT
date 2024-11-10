package com.rohkee.welight.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.rohkee.feat.display.editor.EditorScreen

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Home,
    ) {
        composable<Login> {
        }

        composable<Home> {
            BottomTabSubNavigation(
                onNavigateToDisplayDetail = { id -> },
                onNavigateToCreateNewDisplay = { navController.navigate(DisplayEditor()) },
            )
        }

        composable<DisplayDetail> {
        }

        composable<DisplayEditor> {
            val displayId = it.toRoute<DisplayEditor>().displayId
            EditorScreen(
                onNavigateToDisplayDetail = { id -> },
                onPopBackStack = { navController.popBackStack() },
            )
        }
    }
}
