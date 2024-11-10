package com.rohkee.welight.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rohkee.feat.display.editor.EditorScreen
import com.rohkee.feat.display.editor.navigation.EditorRoute

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
                onNavigateToCreateNewDisplay = { navController.navigate(EditorRoute(null)) },
            )
        }

        composable<DisplayDetail> {
        }

        composable<EditorRoute> {
            EditorScreen(
                onNavigateToDisplayDetail = { id -> },
                onPopBackStack = { navController.popBackStack() },
            )
        }
    }
}
